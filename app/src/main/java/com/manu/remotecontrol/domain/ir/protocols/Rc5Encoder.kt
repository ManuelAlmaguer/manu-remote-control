package com.manu.remotecontrol.domain.ir.protocols

import com.manu.remotecontrol.domain.ir.IrBits
import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrEncoder
import com.manu.remotecontrol.domain.ir.IrEncoding
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.ir.IrWaveformBuilder

/**
 * Philips RC-5 (36 kHz, 14 bits, codificación Manchester con media duración de 889 µs).
 *
 * Estructura de la trama: 2 bits de inicio (siempre a 1), 1 bit de alternancia (toggle),
 * 5 bits de dirección de sistema y 6 bits de comando, todos MSB primero.
 */
class Rc5Encoder : IrEncoder {

    override val protocol: IrProtocolId = IrProtocolId.RC5

    override fun supports(code: IrCode): Boolean = code is IrCode.Rc5

    override fun encode(code: IrCode): IrEncoding {
        val rc5 = code as? IrCode.Rc5
            ?: throw IllegalArgumentException("Rc5Encoder solo admite IrCode.Rc5")
        require(rc5.system in 0..31) { "La dirección RC-5 debe estar en 0..31" }
        require(rc5.command in 0..63) { "El comando RC-5 debe estar en 0..63" }

        val builder = IrWaveformBuilder()
        val frames = rc5.repeats.coerceIn(1, MAX_REPEATS)

        repeat(frames) { frameIndex ->
            if (frameIndex > 0) builder.space(FRAME_GAP_MICROS)

            // Bit de inicio 1 (el segundo bit de inicio va dentro de la secuencia).
            builder.mark(HALF_BIT_MICROS)
            builder.space(HALF_BIT_MICROS)

            // Segundo bit de inicio (siempre 1): espacio + marca.
            IrBits.sendManchester(builder, 1, HALF_BIT_MICROS)
            // Bit de alternancia.
            IrBits.sendManchester(builder, if (rc5.toggle) 1 else 0, HALF_BIT_MICROS)
            // 5 bits de sistema, MSB primero.
            for (i in 4 downTo 0) {
                IrBits.sendManchester(builder, (rc5.system shr i) and 1, HALF_BIT_MICROS)
            }
            // 6 bits de comando, MSB primero.
            for (i in 5 downTo 0) {
                IrBits.sendManchester(builder, (rc5.command shr i) and 1, HALF_BIT_MICROS)
            }
        }

        return IrEncoding(rc5.frequencyHz, builder.build())
    }

    companion object {
        const val HALF_BIT_MICROS = 889
        const val FRAME_GAP_MICROS = 49_000
        const val MAX_REPEATS = 20
    }
}
