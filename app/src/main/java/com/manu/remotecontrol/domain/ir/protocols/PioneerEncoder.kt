package com.manu.remotecontrol.domain.ir.protocols

import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrEncoder
import com.manu.remotecontrol.domain.ir.IrEncoding
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.ir.IrWaveformBuilder

/**
 * Pioneer de 33 bits (40 kHz).
 *
 * Trama: cabecera de 8,5 ms + 4,225 ms, después 33 bits con marca de 500 µs y espacio de
 * 1,5 ms para el 1 y 500 µs para el 0. El contenido es dirección (8 bits) + su complemento
 * + comando (8 bits) + su complemento + un bit a 0, todo LSB primero.
 *
 * Es el protocolo de los receptores, televisores y reproductores Pioneer, y de varios
 * equipos de audio de los años 90-2000.
 */
class PioneerEncoder : IrEncoder {

    override val protocol: IrProtocolId = IrProtocolId.PIONEER

    override fun supports(code: IrCode): Boolean = code is IrCode.Pioneer

    override fun encode(code: IrCode): IrEncoding {
        val pioneer = code as? IrCode.Pioneer
            ?: throw IllegalArgumentException("PioneerEncoder solo admite IrCode.Pioneer")
        require(pioneer.address in 0..0xFF) { "La dirección Pioneer debe estar en 0..255" }
        require(pioneer.command in 0..0xFF) { "El comando Pioneer debe estar en 0..255" }

        val bytes = intArrayOf(
            pioneer.address and 0xFF,
            pioneer.address.inv() and 0xFF,
            pioneer.command and 0xFF,
            pioneer.command.inv() and 0xFF,
            0
        )
        val builder = IrWaveformBuilder()
        val frames = pioneer.repeats.coerceIn(1, MAX_REPEATS)

        repeat(frames) { frameIndex ->
            if (frameIndex > 0) builder.space(FRAME_GAP_MICROS)
            builder.mark(PREAMBLE_MARK_MICROS)
            builder.space(PREAMBLE_SPACE_MICROS)
            for (index in 0 until BIT_COUNT) {
                val bit = (bytes[index / 8] ushr (index % 8)) and 1
                builder.mark(BIT_MARK_MICROS)
                builder.space(if (bit == 1) ONE_SPACE_MICROS else ZERO_SPACE_MICROS)
            }
            builder.mark(BIT_MARK_MICROS)
        }

        return IrEncoding(pioneer.frequencyHz, builder.build())
    }

    companion object {
        const val PREAMBLE_MARK_MICROS = 8_500
        const val PREAMBLE_SPACE_MICROS = 4_225
        const val BIT_MARK_MICROS = 500
        const val ZERO_SPACE_MICROS = 500
        const val ONE_SPACE_MICROS = 1_500
        const val BIT_COUNT = 33
        const val FRAME_GAP_MICROS = 26_000
        const val MAX_REPEATS = 10
    }
}
