package org.ifit.sucks.data

import java.nio.ByteBuffer
import java.nio.ByteOrder


class SetInclineCommand(incline: Int) : CommandPackage() {

    private val speedBytes = ByteBuffer.allocate(Short.SIZE_BYTES).apply {
        order(ByteOrder.LITTLE_ENDIAN)
        putShort(incline.times(100).toShort())
    }.array()

    override val data = byteArrayOf(4, 9, 2, 1, 2) + speedBytes + byteArrayOf(0)
}
