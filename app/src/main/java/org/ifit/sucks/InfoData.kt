package org.ifit.sucks

import org.ifit.sucks.data.BasicInfoResponse
import org.ifit.sucks.data.ExtendedInfoResponse


open class WorkoutInfo(
    private val basicInfo: BasicInfoResponse,
    val extendedInfo: ExtendedInfoResponse
) : Info {
    val time: String
        get() = formatTime(extendedInfo.stageTime)

    val distance: String
        get() = String.format("%.3f", extendedInfo.distance.toFloat() / 1000.0f)

    val pace: String
        get() = formatTime(extendedInfo.refPaceSecPerKm)

    val speed: String
        get() = extendedInfo.refSpeedKmPerH.toString()

    val incline: String
        get() = "${extendedInfo.refIncline}%"
}

class ExerciseInfo(
    val exercise: ExtExercise,
    val time: Int,
    val stepsVal: Pair<Int, Int>
) : Info {

    val exerciseTimeLeft: String
        get() = formatTime(exerciseTimeLeftSeconds)

    val exerciseTimeLeftSeconds: Int
        get() = exercise.durationOfSeconds.minus(time)

    val reps: String
        get() = String.format("%d/%d", exercise.reps.first, exercise.reps.second)

    val steps: String
        get() = String.format("%d/%d", stepsVal.first, stepsVal.second)
}

interface Info {
    fun formatTime(seconds: Int): String {
        val hour = seconds / 3600
        val min = (seconds % 3600) / 60
        val sec = seconds % 60
        return if (hour != 0) {
            String.format("%02d:%02d:%02d", hour, min, sec)
        } else {
            String.format("%d:%02d", min, sec)
        }
    }
}