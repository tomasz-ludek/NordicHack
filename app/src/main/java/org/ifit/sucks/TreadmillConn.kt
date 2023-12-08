package org.ifit.sucks

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import org.ifit.sucks.data.DataPackage
import org.ifit.sucks.data.ResponsePackage
import java.io.IOException
import java.util.concurrent.TimeoutException

interface TreadmillConn {

    fun init(usbManager: UsbManager, device: UsbDevice)

    @Throws(IOException::class, TimeoutException::class)
    suspend fun sendCommand(data: DataPackage): ResponsePackage

    fun disconnect()
}