package org.ifit.sucks.data

abstract class DataPackage {

    abstract val data: ByteArray

    companion object {

        fun toHexString(data: ByteArray): String {
            return data.joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }
        }
    }

    val packet: ByteArray
        get() = data + byteArrayOf(checksum())

    fun toHexString(): String {
        return toHexString(packet)
    }

    private fun checksum(): Byte {
        return data.sum().mod(256).toByte()
    }
}
