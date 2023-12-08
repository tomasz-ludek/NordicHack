package org.ifit.sucks

import android.app.Application
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.XmlStreaming
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import org.ifit.sucks.data.ExtendedInfoResponse
import org.ifit.sucks.data.SetInclineCommand
import org.ifit.sucks.data.SetPaceCommand
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

const val ACTION_USB_PERMISSION = "org.ifit.sucks.USB_PERMISSION"
const val FAKE_MODE = false

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    private val logger = LoggerFactory.getLogger(MainViewModel::class.java)

    private val commandDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val treadmill =
        Treadmill(if (FAKE_MODE) FakeTreadmillConn(app) else RealTreadmillConn())

    var workoutInfo = MutableLiveData<WorkoutInfo>()
    var exerciseInfo = MutableLiveData<ExerciseInfo>()

    var workout: StructuredWorkout? = null

    fun init(usbManager: UsbManager, device: UsbDevice) {
        treadmill.init(usbManager, device)

        viewModelScope.launch(commandDispatcher) {

            var lastExerciseIndex = -1

            while (true) {
                val basicInfo = treadmill.requestBasicInfo()
                val extendedInfo = treadmill.requestExtendedInfo()

                workoutInfo.postValue(WorkoutInfo(basicInfo, extendedInfo))

                val time = extendedInfo.stageTime

                workout?.apply {

                    if (extendedInfo.stage == ExtendedInfoResponse.Stage.WORKOUT) {

                        val exerciseIndex = exerciseIndexForTime(time)
                        val exercise =
                            exerciseFlatList[minOf(exerciseIndex, exerciseFlatList.lastIndex)]

                        if (exerciseIndex > lastExerciseIndex) {

                            if (exerciseIndex <= exerciseFlatList.lastIndex) {

                                if (extendedInfo.refIncline != exercise.incline) {
                                    treadmill.sendCommand(SetInclineCommand(exercise.incline))
                                }
                                if (extendedInfo.refPaceSecPerKm != exercise.paceSecPerKm) {
                                    treadmill.sendCommand(SetPaceCommand(exercise.paceSecPerKm))
                                }
                                logger.info("exercise of structured workout started")

                            } else {

                                logger.info("structured workout finished")
                                treadmill.pauseWorkout()
//                                treadmill.stopTraining()
                            }
                        }

                        val exerciseTime =
                            minOf(time.minus(exercise.startTime), exercise.durationOfSeconds)
                        exerciseInfo.postValue(
                            ExerciseInfo(
                                exercise,
                                exerciseTime,
                                Pair(exerciseIndex + 1, exerciseFlatList.size)
                            )
                        )
                        lastExerciseIndex = exerciseIndex
                    }
                }

                delay(200)
            }
        }
    }

    fun disconnect() {
        treadmill.disconnect()
    }

    fun startWorkout(structuredWorkout: StructuredWorkout? = null) =
        viewModelScope.launch(commandDispatcher) {
            logger.info("starting workout $structuredWorkout")
            if (structuredWorkout != null) {
                workout = structuredWorkout
            }
            treadmill.startWorkout(90)
            logger.info("warm up started")
        }

    fun endWarmUp() = viewModelScope.launch(commandDispatcher) {
        logger.info("ending warm up")
        try {
            treadmill.endWarmUp()
            logger.info("warm up ended")
        } catch (ex: Exception) {
            logger.info("end warm up failed", ex)
        }
    }

    fun pauseWorkout() = viewModelScope.launch(commandDispatcher) {
        logger.info("pausing workout")
        treadmill.pauseWorkout()
        logger.info("workout paused")
    }

    fun resumeWorkout() = viewModelScope.launch(commandDispatcher) {
        logger.info("resuming training")
        try {
            treadmill.resumeTraining()
            logger.info("workout resumed")
        } catch (ex: Exception) {
            logger.info("resume workout failed", ex)
        }
    }

    fun endCoolDown() = viewModelScope.launch(commandDispatcher) {
        // ???
    }

    fun stopWorkout() = viewModelScope.launch(commandDispatcher) {
        logger.info("stopping workout")
        treadmill.stopTraining()
        logger.info("workout stopped")
    }

    fun listStructuredWorkouts(): Array<StructuredWorkout> {
        val assetsPath = "workouts"
        val format = XML {
            xmlVersion = XmlVersion.XML10
            xmlDeclMode = XmlDeclMode.Charset
            indentString = "    "
            autoPolymorphic = true
        }
        val structuredWorkoutList = mutableListOf<StructuredWorkout>()
        app.assets.list(assetsPath)?.forEach {
            val reader =
                XmlStreaming.newGenericReader(app.assets.open("$assetsPath/$it"), "UTF-8")
            val structuredWorkout = format.decodeFromReader<StructuredWorkout>(reader)
            structuredWorkoutList.add(structuredWorkout)
        }
        return structuredWorkoutList.toTypedArray()
        /*
        val format = XML {
            xmlVersion = XmlVersion.XML10
            xmlDeclMode = XmlDeclMode.Charset
            indentString = "    "
            autoPolymorphic = true
        }
        //        val serializedModel = format.encodeToString(Training.serializer(), mainModel.training)
        //        logger.debug(serializedModel)
                val reader = XmlStreaming.newGenericReader(app.assets.open("workouts/test.xml"), "UTF-8")
        //        val serializedModel = format.decodeFromReader(Training.serializer(), reader)

                format.decodeFromReader<Training>(reader)
        */
    }
}
