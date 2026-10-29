package com.manu.remotecontrol.domain.ir.protocols

import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrEncoder
import com.manu.remotecontrol.domain.ir.IrEncoding
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.ir.IrWaveformBuilder

/**
 * Protocolo RAW: patrones de microsegundos arbitrarios.
 *
 * Se usa para códigos aprendidos, conversiones de Pronto hex y equipos con tramas
 * complejas (aires acondicionados Daikin, Gree, Midea...). El patrón debe alternar
 * marca/espacio y empezar por marca.
 */
class RawEncoder : IrEncoder {

    override val protocol: IrProtocolId = IrProtocolId.RAW

    override fun supports(code: IrCode): Boolean = code is IrCode.Raw

    override fun encode(code: IrCode): IrEncoding {
        val raw = code as? IrCode.Raw
            ?: throw IllegalArgumentException("RawEncoder solo admite IrCode.Raw")
        require(raw.pattern.isNotEmpty()) { "El patrón RAW no puede estar vacío" }
        require(raw.pattern.all { it > 0 }) { "Todos los valores RAW deben ser > 0 microsegundos" }

        val builder = IrWaveformBuilder()
        val frames = raw.repeats.coerceIn(1, MAX_REPEATS)
        repeat(frames) { frameIndex ->
            if (frameIndex > 0) builder.space(FRAME_GAP_MICROS)
            raw.pattern.forEachIndexed { index, duration ->
                if (index % 2 == 0) builder.mark(duration) else builder.space(duration)
            }
        }
        return IrEncoding(raw.frequencyHz, builder.build())
    }

    companion object {
        const val FRAME_GAP_MICROS = 40_000
        const val MAX_REPEATS = 20

        /** Convierte una cadena tipo "9000 4500 560 560 ..." en un patrón válido. */
        fun parsePattern(raw: String): List<Int>? {
            val values = raw.trim()
                .split(Regex("[\\s,;]+"))
                .filter { it.isNotBlank() }
                .mapNotNull { it.toIntOrNull() }
            if (values.isEmpty()) return null
            if (values.any { it <= 0 }) return null
            return values
        }
    }
}
