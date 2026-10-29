package com.manu.remotecontrol.domain.ir.protocols

import com.manu.remotecontrol.domain.ir.IrBits
import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrEncoder
import com.manu.remotecontrol.domain.ir.IrEncoding
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.ir.IrWaveformBuilder

/**
 * Protocolo Samsung de 32 bits (el que usan sus televisores y muchos equipos de audio).
 *
 * Trama: 4,5 ms de marca + 4,5 ms de espacio, 32 bits pulse-distance
 * (address + address + command + ~command) y marca final de 560 µs.
 */
class SamsungEncoder : IrEncoder {

    override val protocol: IrProtocolId = IrProtocolId.SAMSUNG

    override fun supports(code: IrCode): Boolean = code is IrCode.Samsung

    override fun encode(code: IrCode): IrEncoding {
        val samsung = code as? IrCode.Samsung
            ?: throw IllegalArgumentException("SamsungEncoder solo admite IrCode.Samsung")
        require(samsung.address in 0..0xFF) { "La dirección Samsung debe estar en 0..255" }
        require(samsung.command in 0..0xFF) { "El comando Samsung debe estar en 0..255" }

        val builder = IrWaveformBuilder()
        val frames = samsung.repeats.coerceIn(1, MAX_REPEATS)

        repeat(frames) { frameIndex ->
            if (frameIndex > 0) builder.space(FRAME_GAP_MICROS)
            builder.mark(HEADER_MARK_MICROS)
            builder.space(HEADER_SPACE_MICROS)
            IrBits.sendLsbFirst(builder, samsung.address and 0xFF, 8, BIT_MARK_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)
            IrBits.sendLsbFirst(builder, samsung.address and 0xFF, 8, BIT_MARK_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)
            IrBits.sendLsbFirst(builder, samsung.command and 0xFF, 8, BIT_MARK_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)
            IrBits.sendLsbFirst(builder, samsung.command.inv() and 0xFF, 8, BIT_MARK_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)
            builder.mark(BIT_MARK_MICROS)
        }

        return IrEncoding(samsung.frequencyHz, builder.build())
    }

    companion object {
        const val HEADER_MARK_MICROS = 4_500
        const val HEADER_SPACE_MICROS = 4_500
        const val BIT_MARK_MICROS = 560
        const val ZERO_SPACE_MICROS = 560
        const val ONE_SPACE_MICROS = 1_690
        const val FRAME_GAP_MICROS = 46_000
        const val MAX_REPEATS = 20
    }
}
