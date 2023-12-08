package org.ifit.sucks.data

/**
 * 04 10 02 00 0a 1b 94 30 00 00 40 50 00 80 18 27
 */
class ExtendedInfoRequest : ResponsePackage() {
    override val data =
        byteArrayOf(4, 16, 2, 0, 10, 27, -108, 48, 0, 0, 64, 80, 0, -128, 24)
}
