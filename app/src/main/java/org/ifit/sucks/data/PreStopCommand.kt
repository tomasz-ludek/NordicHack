package org.ifit.sucks.data

/**
 * 04 09 02 02 00 10 04 00 25
 */
class PreStopCommand : CommandPackage() {
    override val data = byteArrayOf(4, 9, 2, 2, 0, 16, 4, 0)
}
