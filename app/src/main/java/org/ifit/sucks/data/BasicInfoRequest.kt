package org.ifit.sucks.data

/**
 * 04 15 02 00 0f 80 0a 41 00 00 00 00 00 00 00 00 00 85 00 10 8a
 */
class BasicInfoRequest : ResponsePackage() {
    override val data =
        byteArrayOf(4, 21, 2, 0, 15, -128, 10, 65, 0, 0, 0, 0, 0, 0, 0, 0, 0, -123, 0, 16)
}
