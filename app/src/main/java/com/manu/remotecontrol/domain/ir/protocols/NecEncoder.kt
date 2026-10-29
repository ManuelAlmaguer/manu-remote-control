package com.manu.remotecontrol.domain.ir.protocols

import com.manu.remotecontrol.domain.ir.IrBits
import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrEncoder
import com.manu.remotecontrol.domain.ir.IrEncoding
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.ir.IrWaveformBuilder

/**
 * Protocolo NEC y su variante extendida.
 *
 * Trama: 9 ms de marca + 4,5 ms de espacio, 32 bits pulse-distance (560 µs de marca,
 * 560 µs para el 0 y 1690 µs para el 1) y una marca final de 560 µs.
 *
 * - NEC clásico: address(8) + ~address(8) + command(8) + ~command(8).
 * - NEC extendido: address(16, LSB primero) + command(8) + ~command(8). Es el que usan
 *   TCL, Hisense y muchos aires acondicionados LG.
 */
class NecEncoder : IrEncoder {

    override val protocol: IrProtocolId = IrProtocolId.NEC

    override val aliases: List<IrProtocolId> = listOf(IrProtocolId.NEC_EXTENDED)

    override fun supports(code: IrCode): Boolean = code is IrCode.Nec

    override fun encode(code: IrCode): IrEncoding {
        val nec = code as? IrCode.Nec
            ?: throw IllegalArgumentException("NecEncoder solo admite IrCode.Nec")
        require(nec.command in 0..0xFF) { "El comando NEC debe estar en 0..255" }
        require(nec.address in 0..0xFFFF) { "La dirección NEC debe estar en 0..65535" }

        val builder = IrWaveformBuilder()
        val frames = nec.repeats.coerceIn(1, MAX_REPEATS)

        repeat(frames) { frameIndex ->
            if (frameIndex > 0) builder.space(FRAME_GAP_MICROS)
            builder.mark(HEADER_MARK_MICROS)
            builder.space(HEADER_SPACE_MICROS)

            if (nec.extended) {
                IrBits.sendLsbFirst(builder, nec.address and 0xFFFF, 16, BIT_MARK_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)
            } else {
                IrBits.sendLsbFirst(builder, nec.address and 0xFF, 8, BIT_MARK_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)
                IrBits.sendLsbFirst(builder, nec.address.inv() and 0xFF, 8, BIT_MARK_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)
            }
            IrBits.sendLsbFirst(builder, nec.command and 0xFF, 8, BIT_MARK_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)
            IrBits.sendLsbFirst(builder, nec.command.inv() and 0xFF, 8, BIT_MARK_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)

            builder.mark(BIT_MARK_MICROS)
        }

        return IrEncoding(nec.frequencyHz, builder.build())
    }

    companion object {
        const val HEADER_MARK_MICROS = 9_000
        const val HEADER_SPACE_MICROS = 4_500
        const val BIT_MARK_MICROS = 560
        const val ZERO_SPACE_MICROS = 560
        const val ONE_SPACE_MICROS = 1_690
        const val FRAME_GAP_MICROS = 40_000
        const val MAX_REPEATS = 20
    }
}
