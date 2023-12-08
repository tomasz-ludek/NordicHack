package org.ifit.sucks.data

/**
 * 04 1c 02 09 00 00 00 02 18 40 00 00 81 20 1c 58 02 01 3c 00 a0 00 00 00 58 02 00 d3
 */
class Start2Package : DataPackage() {

    override val data =
        byteArrayOf(
            4, 28, 2, 9, 0, 0, 0, 2, 24, 64, 0, 0, -127, 32,
            28, 88, 2, 1, 60, 0, -96, 0, 0, 0, 88, 2, 0
        )
}