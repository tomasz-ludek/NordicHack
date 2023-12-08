package org.ifit.sucks.data

import java.nio.ByteBuffer

/**
 * 04 05 02 02 0d ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff
 */
class CommandAckResponse(override val data: ByteArray) : ResponsePackage() {

    companion object {

        fun isHeaderCorrect(buffer: ByteBuffer): Boolean {
            val header = byteArrayOf(4, 5, 2, 2)
            return buffer.array().copyOfRange(0, 4).contentEquals(header)
        }
    }
}
