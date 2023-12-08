package org.ifit.sucks.data

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.Duration
import kotlin.math.roundToInt


open class SetSpeedRequest(private val metersPerHour: Int) : CommandPackage() {

    companion object {
        fun paceToSpeed(pace: Int): Int {
            val time = pace / 60.0
            val speed = (60 / time * 1000)
            return speed.roundToInt()
        }
    }

    private val speedBytes = ByteBuffer.allocate(Short.SIZE_BYTES).apply {
        order(ByteOrder.LITTLE_ENDIAN)
        val speedValue = (metersPerHour / 10.0).roundToInt()
        putShort(speedValue.toShort())
    }.array()

    override val data = byteArrayOf(4, 9, 2, 1, 1) + speedBytes + byteArrayOf(0)
}
