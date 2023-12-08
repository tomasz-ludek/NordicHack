package org.ifit.sucks.data

/**
 * 04 1c 02 09 00 00 40 02 18 40 00 00 80 b4 00 00 00 20 1c 58 02 01 3c 00 58 02 00 26
 */
class SetupWarmUpCommand(warmUpTime : Int) : DataPackage() {

    override val data =
        byteArrayOf(
            4, 28, 2, 9, 0, 0, 64, 2, 24, 64, 0, 0, -128, -76,
            0, 0, 0, 32, 28, 88, 2, 1, warmUpTime.toByte(), 0, 88, 2, 0
        )
}
