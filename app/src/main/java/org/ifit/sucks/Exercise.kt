package org.ifit.sucks

import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import kotlin.math.roundToInt

@Serializable
sealed interface BaseExercise

class ExtExercise(
    exercise: Exercise,
    val startTime: Int,
    val reps: Pair<Int, Int>,
) : Exercise(exercise.duration, exercise.incline, exercise.pace)

@Serializable
open class Exercise(
    val duration: String,
    val incline: Int,
    val pace: String
) : BaseExercise {

    val paceSecPerKm: Int
        get() {
            val paceParts = pace.split(":")
            val min = paceParts[0].toInt()
            val sec = paceParts[1].toInt()
            return min.times(60).plus(sec)
        }

    val durationOfSeconds: Int by lazy {
        val durationParts = duration.split(":")
        val min = durationParts[0].toInt()
        val sec = durationParts[1].toInt()
        min.times(60).plus(sec)
    }

    val speed: Int
        get() {
            val time = paceSecPerKm / 60.0
            val speed = (60 / time * 1000)
            return speed.roundToInt()
        }
}

@Serializable
class ExerciseSet(
    val reps: Int,
    val exerciseList: List<Exercise>,
) : BaseExercise

@Serializable
class StructuredWorkout(val title: String, val exerciseList: List<BaseExercise>) {

    private val logger = LoggerFactory.getLogger(StructuredWorkout::class.java)

    val exerciseFlatList: List<ExtExercise> by lazy {
        val result = mutableListOf<ExtExercise>()
        var startTime = 0
        exerciseList.forEach { exercise ->
            when (exercise) {
                is Exercise -> {
                    result.add(ExtExercise(exercise, startTime, Pair(0, 0)))
                    startTime += exercise.durationOfSeconds
                }

                is ExerciseSet -> {
                    val totalReps = exercise.reps
                    for (rep in 1..totalReps) {
                        exercise.exerciseList.forEach {
                            val copyOfExercise =
                                ExtExercise(it, startTime, Pair(rep, totalReps))
                            result.add(copyOfExercise)
                            startTime += it.durationOfSeconds
                        }
                    }
                }
            }
        }
        result
    }

    fun exerciseIndexForTime(time: Int): Int {
        var exerciseEndTime = 0
        exerciseFlatList.forEachIndexed { index, exercise ->
            exerciseEndTime += exercise.durationOfSeconds
            if (exerciseEndTime > time) {
                return index
            }
        }
        return exerciseFlatList.size
    }
}