package org.ifit.sucks.data

import java.nio.ByteBuffer

/**
 * 04 25 02 02 00 00 ff ff ff ff ff ff ff ff 00 00 00 00 32 83 00 00 00 a4 01 b4 00 00 00 00 00 00 00 00 00 01 34 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff
 */
class BasicInfoResponse(override val data: ByteArray) : ResponsePackage() {

    companion object {

        fun isHeaderCorrect(buffer: ByteBuffer): Boolean {
            val header = byteArrayOf(4, 37, 2, 2)
            return buffer.array().copyOfRange(0, 4).contentEquals(header)
        }
    }

    private val buffer = ByteBuffer.wrap(data)

    val time: Int
        get() = buffer.getShort(19).toInt()

    val actSpeedRaw: Int
        get() = buffer.getShort(23).toInt()

    val actSpeed: Float
        get() = actSpeedRaw.toFloat() / 100
}
