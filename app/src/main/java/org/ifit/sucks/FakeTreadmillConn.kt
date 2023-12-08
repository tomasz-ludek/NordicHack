package org.ifit.sucks

import android.app.Application
import android.content.Context
import android.hardware.usb.*
import org.ifit.sucks.data.*
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.TimeoutException

class FakeTreadmillConn(val app: Application) : TreadmillConn {

    private val logger = LoggerFactory.getLogger(FakeTreadmillConn::class.java)

    private val forceClaim = true

    private var basicInfoSamples = mutableListOf<ByteArray>()
    private var basicInfoSamplesIndex = 0

    private var extendedInfoSamples = mutableListOf<ByteArray>()
    private var extendedInfoSamplesIndex = 0

    fun init() {
        app.assets.open("sample_data").bufferedReader().useLines { lines ->
            val basicInfoPacketPrefix = "04,25,02,02"
            val extendedInfoPacketPrefix = "04,2e,02,02"
            lines.filter {
                it.contains(basicInfoPacketPrefix) or it.contains(extendedInfoPacketPrefix)
            }.forEach { line ->
                var packet = line.substringAfter("DA:").substringBefore(";6P:")
                if (packet.startsWith(basicInfoPacketPrefix)) {
                    basicInfoSamples.add(packet.decodeHex())
                } else {
                    extendedInfoSamples.add(packet.decodeHex())
                }
            }
        }
    }

    override fun init(usbManager: UsbManager, device: UsbDevice) {
        logger.debug("fake conn init")
    }

    @Throws(IOException::class, TimeoutException::class)
    override suspend fun sendCommand(data: DataPackage): ResponsePackage {
        logger.info("fake conn sending command ${data::class.simpleName}")

        return when (data) {
            is BasicInfoRequest -> basicInfoResponse()
            is ExtendedInfoRequest -> extendedInfoResponse()
            is CommandPackage -> CommandAckResponse(ByteArray(10))
            is SetupWarmUpCommand -> CommandAckResponse(ByteArray(10))
            is StartCommand -> CommandAckResponse(ByteArray(10))
            is ResumeCommand -> CommandAckResponse(ByteArray(10))
            is StopCommand -> CommandAckResponse(ByteArray(10))
            else -> {
                val ex = IOException("unsupported request $data")
                logger.warn(ex.message)
                throw ex
            }
        }
    }

    private fun basicInfoResponse(): BasicInfoResponse {
        val packet = basicInfoSamples[basicInfoSamplesIndex]
        if (basicInfoSamplesIndex < basicInfoSamples.lastIndex) {
            basicInfoSamplesIndex++
        } else {
            basicInfoSamplesIndex = 0
        }
        logger.debug(packet.joinToString(","))
        return BasicInfoResponse(packet)
    }

    private fun extendedInfoResponse(): ExtendedInfoResponse {
        val packet = extendedInfoSamples[extendedInfoSamplesIndex]
        if (extendedInfoSamplesIndex < extendedInfoSamples.lastIndex) {
            extendedInfoSamplesIndex++
        } else {
            extendedInfoSamplesIndex = 0
        }
        logger.debug(packet.joinToString(","))
        return ExtendedInfoResponse(packet)
    }

    override fun disconnect() {
        logger.debug("fake conn disconnect")
    }

    fun String.decodeHex(): ByteArray {
        return split(",")
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }
}