package org.ifit.sucks

import android.hardware.usb.*
import kotlinx.coroutines.delay
import org.ifit.sucks.data.*
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.ByteBuffer
import java.time.LocalTime
import java.util.concurrent.TimeoutException

const val TIMEOUT = 1000L

class RealTreadmillConn : TreadmillConn {

    private val logger = LoggerFactory.getLogger(RealTreadmillConn::class.java)

    private lateinit var usbInterface: UsbInterface
    private lateinit var usbInEndpoint: UsbEndpoint
    private lateinit var usbOutEndpoint: UsbEndpoint
    private lateinit var usbConnection: UsbDeviceConnection

    private val forceClaim = true

    private var startTime = System.currentTimeMillis().div(1000)

    override fun init(usbManager: UsbManager, device: UsbDevice) {
        usbInterface = device.getInterface(0)
        val endpoint0 = usbInterface.getEndpoint(0)
        val endpoint1 = usbInterface.getEndpoint(1)
        if (endpoint0.direction == UsbConstants.USB_DIR_OUT) {
            usbOutEndpoint = endpoint0
            usbInEndpoint = endpoint1
        } else {
            usbOutEndpoint = endpoint1
            usbInEndpoint = endpoint0
        }
        usbConnection = usbManager.openDevice(device)
        usbConnection.claimInterface(usbInterface, forceClaim)
    }

    @Throws(IOException::class, TimeoutException::class)
    override suspend fun sendCommand(data: DataPackage): ResponsePackage {

        val time = System.currentTimeMillis().div(1000).minus(startTime)

        logger.info("sending command ${data::class.simpleName}\t(${LocalTime.ofSecondOfDay(time)})")
        var request = UsbRequest()
        if (!request.initialize(usbConnection, usbOutEndpoint)) {

            //@{String.format(`%d:%02d`, viewModel.infoData.pace.minute, viewModel.infoData.pace.second)}
            logger.warn("CRASH!@#$%^&* - delay\t(${LocalTime.ofSecondOfDay(time)})")
            delay(500)

            if (!request.initialize(usbConnection, usbOutEndpoint)) {

                logger.warn("still CRASH!@#$%^&* - kill")

                val endpoint0 = usbInterface.getEndpoint(0)
                val endpoint1 = usbInterface.getEndpoint(1)
                logger.warn("$endpoint0")
                logger.warn("$endpoint1")
                if (endpoint0.direction == UsbConstants.USB_DIR_OUT) {
                    usbOutEndpoint = endpoint0
                    usbInEndpoint = endpoint1
                } else {
                    usbOutEndpoint = endpoint1
                    usbInEndpoint = endpoint0
                }
                val ex = IOException("OUT request initialization failed ${data.toHexString()}")
                logger.warn("${ex.message}")
                throw ex
            }
        }
        try {
            request.queue(ByteBuffer.wrap(data.packet))
            logger.debug("tx: ${data.toHexString()}")

            usbConnection.requestWait(TIMEOUT)

        } catch (ex: TimeoutException) {
            logger.warn("unable to send data", ex)
            throw ex
        }

        logger.debug("command sent, getting response")
        request = UsbRequest()
        if (!request.initialize(usbConnection, usbInEndpoint)) {
            val ex = IOException("IN request initialization failed")
            logger.warn("${ex.message}")
            throw ex
        }
        val buffer = ByteBuffer.allocate(usbInEndpoint.maxPacketSize)
        try {
            request.queue(buffer)

            usbConnection.requestWait(TIMEOUT)
            logger.debug("rx: ${DataPackage.toHexString(buffer.array())}")

        } catch (ex: TimeoutException) {
            logger.warn("device didn't respond", ex)
            throw ex
        }

        val responseData = buffer.array()

        return when (responseData[1].toInt()) {
            0x05 -> CommandAckResponse(responseData)
            0x25 -> BasicInfoResponse(responseData)
            0x2e -> ExtendedInfoResponse(responseData)
            else -> {
                val ex = IOException(
                    "unsupported response received ${DataPackage.toHexString(responseData)}"
                )
                logger.warn(ex.message)
                UnknownResponse(responseData)
            }
        }
    }

    override fun disconnect() {
        if (::usbConnection.isInitialized) {
            usbConnection.releaseInterface(usbInterface)
            usbConnection.close()
        }
    }
}