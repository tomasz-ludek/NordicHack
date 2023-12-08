package org.ifit.sucks.data

/**
 * 04 13 02 0c 00 00 00 00 00 00 00 00 00 00 00 80 01 00 a6
 */
class Refresh1Command : CommandPackage() {
    override val data =
        byteArrayOf(4, 19, 2, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 1, 0)
}
