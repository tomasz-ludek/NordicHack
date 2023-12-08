package org.ifit.sucks.data

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

/**
 * 04 2e 02 02 90 01 90 01 92 00 c4 00 00 00 00 00 00 00 02 8c 00 8c 00 00 00 ea b2 17 00 3c 00 6f 00 ac 00 58 02 a4 6a 00 00 a4 6a 00 00 48 ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff ff
 */
class ExtendedInfoResponse(override val data: ByteArray) : ResponsePackage() {

    companion object {

        fun isHeaderCorrect(buffer: ByteBuffer): Boolean {
            val header = byteArrayOf(4, 46, 2, 2)
            return buffer.array().copyOfRange(0, 4).contentEquals(header)
        }
    }

    enum class Stage(val value: Int) {
        IDLE(0x01),
        WARM_UP(0x0a),
        WORKOUT(0x02),
        COOL_DOWN(0x0b),
        PAUSE(0x03),
        PRE_STOP(0x04),
        EMERGENCY(0x08),
        UNKNOWN(0x00);

        companion object {
            fun of(value: Int): Stage {
                Stage.values().forEach {
                    if (it.value == value) {
                        return it
                    }
                }
                return Stage.UNKNOWN
            }
        }
    }

    private val buffer = ByteBuffer.wrap(data).apply {
        order(ByteOrder.LITTLE_ENDIAN)
    }

    val refSpeedRaw: Int
        get() = buffer.getShort(4).toInt()

    val refSpeedKmPerH: Float
        get() = refSpeedRaw.toFloat() / 100

    val refPaceSecPerKm: Int
        get() {
            return if (refSpeedKmPerH > 0)
                (3600.0 / refSpeedKmPerH).roundToInt()
            else
                0
        }

    val refInclineRaw: Int
        get() = buffer.getShort(6).toInt()

    val refIncline: Int
        get() = refInclineRaw / 100

    val power: Int
        get() = buffer.getShort(8).toInt()

    val distance: Int
        get() = buffer.getShort(10).toInt()

    val unused1: Int
        get() = buffer.getShort(12).toInt()

    val unused2: Int
        get() = buffer.getShort(14).toInt()

    val unused3: Int
        get() = buffer.getShort(16).toInt()

    val stageRaw: Int
        get() = buffer.get(18).toInt()

    val stage: Stage
        get() = Stage.of(stageRaw)

    val lapTime: Int
        get() = buffer.getShort(19).toInt()

    val stageTime: Int
        get() = buffer.getShort(21).toInt()

    val unused4: Int
        get() = buffer.getShort(23).toInt()

    val unknown1: Int
        get() = buffer.get(25).toInt()

    val unknown2a: Int
        get() = buffer.get(26).toInt()

    val unknown2b: Int
        get() = buffer.get(27).toInt()

    val unused5: Int
        get() = buffer.get(28).toInt()

    val unknown3: Int
        get() = buffer.getShort(29).toInt()

    val unknown4: Int
        get() = buffer.getShort(31).toInt()

    val unknown5: Int
        get() = buffer.getShort(33).toInt()

    val unknown6: Int
        get() = buffer.get(35).toInt()

    val unknown7: Int
        get() = buffer.get(36).toInt()

    val elevation1: Int
        get() = buffer.getShort(37).toInt()

    val unused6: Int
        get() = buffer.get(39).toInt()

    val elevation2: Int
        get() = buffer.getShort(41).toInt()
}
