package org.ifit.sucks.data

import kotlin.math.roundToInt

class SetPaceCommand(private val paceSecPerKm: Int) :
    SetSpeedRequest(paceToSpeed(paceSecPerKm)) {

    fun paceToSpeed(pace: Int): Int {
        val time = pace / 60.0
        val speed = (60 / time * 1000)
        return speed.roundToInt()
    }
}
