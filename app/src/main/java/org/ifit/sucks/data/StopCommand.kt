package org.ifit.sucks.data


/**
 * 04 0b 02 02 02 10 00 00 01 00 26
 */
class StopCommand : DataPackage() {
    override val data = byteArrayOf(4, 11, 2, 2, 2, 16, 0, 0, 1, 0)
}
