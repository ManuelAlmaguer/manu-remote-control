package com.manu.remotecontrol.domain.ir.protocols

import com.manu.remotecontrol.domain.ir.IrBits
import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrEncoder
import com.manu.remotecontrol.domain.ir.IrEncoding
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.ir.IrWaveformBuilder

/**
 * Sony SIRC en sus variantes de 12, 15 y 20 bits (40 kHz).
 *
 * Trama: marca de 2,4 ms + espacio de 600 µs, después pulse-width:
 * un `1` es 1200 µs de marca y un `0` es 600 µs de marca, siempre con 600 µs de espacio.
 * Los 7 bits de comando se envían primero (LSB) y después la dirección del equipo.
 */
class SonySircEncoder : IrEncoder {

    override val protocol: IrProtocolId = IrProtocolId.SONY_SIRC

    override fun supports(code: IrCode): Boolean = code is IrCode.Sony

    override fun encode(code: IrCode): IrEncoding {
        val sony = code as? IrCode.Sony
            ?: throw IllegalArgumentException("SonySircEncoder solo admite IrCode.Sony")
        require(sony.bits in SUPPORTED_BIT_LENGTHS) {
            "Sony SIRC solo admite 12, 15 o 20 bits (recibido ${sony.bits})"
        }
        require(sony.command in 0..0x7F) { "El comando SIRC debe estar en 0..127" }

        val builder = IrWaveformBuilder()
        val frames = sony.repeats.coerceIn(1, MAX_REPEATS)
        val deviceBitCount = when (sony.bits) {
            12 -> 5
            15 -> 8
            else -> 13
        }

        repeat(frames) { frameIndex ->
            if (frameIndex > 0) builder.space(FRAME_GAP_MICROS)
            builder.mark(HEADER_MARK_MICROS)
            builder.space(BIT_SPACE_MICROS)

            // 7 bits de comando, LSB primero.
            for (i in 0 until 7) {
                sendBit(builder, (sony.command shr i) and 1)
            }
            // Bits de dirección del equipo, LSB primero (13 bits en SIRC20).
            for (i in 0 until deviceBitCount) {
                sendBit(builder, (sony.device shr i) and 1)
            }
        }

        return IrEncoding(sony.frequencyHz, builder.build())
    }

    private fun sendBit(builder: IrWaveformBuilder, bit: Int) {
        builder.mark(if (bit == 1) ONE_MARK_MICROS else ZERO_MARK_MICROS)
        builder.space(BIT_SPACE_MICROS)
    }

    companion object {
        const val HEADER_MARK_MICROS = 2_400
        const val BIT_SPACE_MICROS = 600
        const val ZERO_MARK_MICROS = 600
        const val ONE_MARK_MICROS = 1_200
        const val FRAME_GAP_MICROS = 25_000
        const val MAX_REPEATS = 20
        val SUPPORTED_BIT_LENGTHS = setOf(12, 15, 20)
    }
}
