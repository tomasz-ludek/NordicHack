package org.ifit.sucks.data

/**
 * 04 13 02 0c 00 00 00 00 00 00 00 00 00 00 00 80 00 00 a5
 */
class Refresh2Command : CommandPackage() {
    override val data =
        byteArrayOf(4, 19, 2, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -128, 0, 0)
}
