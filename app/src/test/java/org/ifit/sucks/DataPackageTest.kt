package org.ifit.sucks

import org.ifit.sucks.data.*
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * description
 *
 */
class DataPackageTest {

    @Test
    fun endWarmUpCommand_dataCorrect() {
        assertEquals("04 0d 02 02 03 10 c8 00 00 00 02 00 f2", EndWarmUpCommand().toHexString())
    }

    @Test
    fun resumeCommand_dataCorrect() {
        assertEquals("04 09 02 02 00 10 0d 00 2e", ResumeCommand().toHexString())
    }

    @Test
    fun pauseCommand_dataCorrect() {
        assertEquals("04 09 02 02 00 10 03 00 24", PauseCommand().toHexString())
    }

    @Test
    fun preStopCommand_dataCorrect() {
        assertEquals("04 09 02 02 00 10 04 00 25", PreStopCommand().toHexString())
    }

    @Test
    fun stopCommand_dataCorrect() {
        assertEquals("04 0b 02 02 02 10 00 00 01 00 26", StopCommand().toHexString())
    }

    @Test
    fun refresh1Command_dataCorrect() {
        assertEquals(
            "04 13 02 0c 00 00 00 00 00 00 00 00 00 00 00 80 01 00 a6",
            Refresh1Command().toHexString()
        )
    }

    @Test
    fun refresh2Command_dataCorrect() {
        assertEquals(
            "04 13 02 0c 00 00 00 00 00 00 00 00 00 00 00 80 00 00 a5",
            Refresh2Command().toHexString()
        )
    }

    @Test
    fun start1Package_dataCorrect() {
        assertEquals(
            "04 1c 02 09 00 00 40 02 18 40 00 00 80 30 2a 00 00 20 1c 58 02 01 3c 00 58 02 00 cc",
            Start1Command().toHexString()
        )
    }

    @Test
    fun start2Package_dataCorrect() {
        assertEquals(
            "04 1c 02 09 00 00 00 02 18 40 00 00 81 20 1c 58 02 01 3c 00 a0 00 00 00 58 02 00 d3",
            Start2Package().toHexString()
        )
    }

    @Test
    fun start3Package_dataCorrect() {
        assertEquals(
            "04 1c 02 09 00 00 40 02 18 40 00 00 80 30 2a 00 00 20 1c 58 02 01 b4 00 58 02 00 44",
            Start3Package().toHexString()
        )
    }

    @Test
    fun startCommand_dataCorrect() {
        assertEquals(
            "04 0d 02 02 03 10 c8 00 00 00 0a 00 fa",
            StartCommand().toHexString()
        )
    }

    @Test
    fun setupWarmUpCommand_dataCorrect() {
        assertEquals(
            "04 1c 02 09 00 00 40 02 18 40 00 00 80 b4 00 00 00 20 1c 58 02 01 3c 00 58 02 00 26",
            SetupWarmUpCommand(60).toHexString()
        )
    }

    @Test
    fun getStatusPackage_dataCorrect() {
        assertEquals(
            "04 15 02 00 0f 80 0a 41 00 00 00 00 00 00 00 00 00 85 00 10 8a",
            BasicInfoRequest().toHexString()
        )
    }

    @Test
    fun getStatus2Package_dataCorrect() {
        assertEquals(
            "04 10 02 00 0a 1b 94 30 00 00 40 50 00 80 18 27",
            ExtendedInfoRequest().toHexString()
        )
    }

    @Test
    fun speedPackage_dataCorrect() {
//        assertEquals("04 09 02 01 01 e8 03 00 fc", SetSpeedRequest(1000).toHexString())    //1000m/h
//        assertEquals("04 09 02 01 01 b0 04 00 c5", SetSpeedRequest(1200).toHexString())    //1200m/h
//        assertEquals("04 09 02 01 01 40 06 00 57", SetSpeedRequest(1600).toHexString())    //1600m/h
//        assertEquals("04 09 02 01 01 08 07 00 20", SetSpeedRequest(1800).toHexString())    //1800m/h
//        assertEquals("04 09 02 01 01 d0 07 00 e8", SetSpeedRequest(2000).toHexString())    //1800m/h
    }

    @Test
    fun inclinePackage_dataCorrect() {
        assertEquals("04 09 02 01 02 64 00 00 76", SetInclineCommand(100).toHexString())    //1.00%
        assertEquals("04 09 02 01 02 c8 00 00 da", SetInclineCommand(200).toHexString())    //2.00%
        assertEquals("04 09 02 01 02 2c 01 00 3f", SetInclineCommand(300).toHexString())    //3.00%
        assertEquals("04 09 02 01 02 f4 01 00 07", SetInclineCommand(500).toHexString())    //5.00%
        assertEquals("04 09 02 01 02 e8 03 00 fd", SetInclineCommand(1000).toHexString())   //10.00%
    }

    @Test
    fun extendedInfoPackage_dataCorrect() {
        val packet =
            "04 2e 02 02 20 03 64 00 7a 00 29 00 00 00 00 00 00 00 02 3c 00 3c 00 00 00 7e 32 04 00 3c 00 19 00 62 00 58 02 a4 06 00 00 a4 06 00 00 f3 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff"
        val data = packet.decodeHex()
        val extendedInfo = ExtendedInfoResponse(data)
        assertEquals(800, extendedInfo.refSpeedRaw)
        assertEquals(8.0f, extendedInfo.refSpeed)
        assertEquals(100, extendedInfo.refInclineRaw)
        assertEquals(1.0f, extendedInfo.refIncline)
        assertEquals(122, extendedInfo.power)
        assertEquals(41, extendedInfo.distance)
        assertEquals(2, extendedInfo.stage)
        assertEquals(60, extendedInfo.lapTime)
        assertEquals(60, extendedInfo.stageTime)
        assertEquals(1700, extendedInfo.elevation1)
        assertEquals(1700, extendedInfo.elevation2)
    }

    fun String.decodeHex(): ByteArray {
        return split(" ")
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    @Test
    fun convertMinPerKmToKMPerH_dataCorrect() {
//        assertEquals(SetSpeedRequest.convert(8, 0), 7500)
//        assertEquals(SetSpeedRequest.convert(4, 0), 15000)
//        assertEquals(SetSpeedRequest.convert(2, 0), 30000)
//        assertEquals(SetSpeedRequest.convert(5, 0), 12000)
//        assertEquals(SetSpeedRequest.convert(2, 30), 24000)
//        assertEquals(SetSpeedRequest.convert(4, 10), 14400)
//        assertEquals(SetSpeedRequest.convert(4, 30), 13333)
    }
}