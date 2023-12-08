package org.ifit.sucks.data

/**
 * 04 0d 02 02 03 10 c8 00 00 00 02 00 f2
 */
class EndWarmUpCommand : CommandPackage() {

    override val data = byteArrayOf(4, 13, 2, 2, 3, 16, -56, 0, 0, 0, 2, 0)
}
