package org.ifit.sucks

import android.hardware.usb.*
import org.ifit.sucks.data.*
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.TimeoutException

class Treadmill(private val treadmillConn: TreadmillConn) {

    private val logger = LoggerFactory.getLogger(Treadmill::class.java)

    fun init(usbManager: UsbManager, device: UsbDevice) {
        treadmillConn.init(usbManager, device)
    }

    suspend fun requestBasicInfo(): BasicInfoResponse {
        return sendCommand(BasicInfoRequest()) as BasicInfoResponse
    }

    suspend fun requestExtendedInfo(): ExtendedInfoResponse {
        return sendCommand(ExtendedInfoRequest()) as ExtendedInfoResponse
    }

    @Throws(IOException::class, TimeoutException::class)
    suspend fun startWorkout(warmUpTime: Int) {
        sendCommand(Refresh1Command())
        sendCommand(Refresh2Command())
        logger.info("warmUpTime: $warmUpTime")
//        sendCommand(SetupWarmUpCommand(warmUpTime))
        sendCommand(StartCommand())
    }

    @Throws(IOException::class, TimeoutException::class)
    suspend fun endWarmUp() {
        sendCommand(EndWarmUpCommand())
    }

    @Throws(IOException::class, TimeoutException::class)
    suspend fun pauseWorkout() {
        sendCommand(PauseCommand())
    }

    @Throws(IOException::class, TimeoutException::class)
    suspend fun resumeTraining() {
        sendCommand(ResumeCommand())
    }

    @Throws(IOException::class, TimeoutException::class)
    suspend fun stopTraining() {
        sendCommand(PreStopCommand())
        sendCommand(StopCommand())
    }

    @Throws(IOException::class, TimeoutException::class)
    suspend fun sendCommand(data: DataPackage): ResponsePackage {
        return treadmillConn.sendCommand(data)
    }

    fun disconnect() {
        treadmillConn.disconnect()
    }
}