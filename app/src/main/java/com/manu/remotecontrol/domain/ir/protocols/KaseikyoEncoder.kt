package com.manu.remotecontrol.domain.ir.protocols

import com.manu.remotecontrol.domain.ir.IrBits
import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrEncoder
import com.manu.remotecontrol.domain.ir.IrEncoding
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.ir.IrWaveformBuilder

/**
 * Kaseikyo / Panasonic de 48 bits (37 kHz, unidad de 432 µs).
 *
 * Estructura de la trama (6 bytes, bit menos significativo primero):
 * - byte 0-1: vendor id de 16 bits.
 * - byte 2: paridad del vendor (4 bits) + genre1 (4 bits).
 * - byte 3: genre2 (4 bits) + 4 bits bajos del comando.
 * - byte 4: id (2 bits) + 6 bits altos del comando.
 * - byte 5: paridad (XOR de los bytes 2, 3 y 4).
 *
 * Es el protocolo de los televisores, aires y equipos de audio Panasonic, así como de
 * los mandos que usan el mismo chip (Technics, algunos JVC y Denon).
 */
class KaseikyoEncoder : IrEncoder {

    override val protocol: IrProtocolId = IrProtocolId.KASEIKYO

    override fun supports(code: IrCode): Boolean = code is IrCode.Kaseikyo

    override fun encode(code: IrCode): IrEncoding {
        val kaseikyo = code as? IrCode.Kaseikyo
            ?: throw IllegalArgumentException("KaseikyoEncoder solo admite IrCode.Kaseikyo")
        require(kaseikyo.command in 0..0x3FF) { "El comando Kaseikyo debe estar en 0..1023" }

        val bytes = toDataBytes(kaseikyo.address, kaseikyo.command)
        val builder = IrWaveformBuilder()
        val frames = kaseikyo.repeats.coerceIn(1, MAX_REPEATS)

        repeat(frames) { frameIndex ->
            if (frameIndex > 0) builder.space(FRAME_GAP_MICROS)
            builder.mark(PREAMBLE_MARK_MICROS)
            builder.space(PREAMBLE_SPACE_MICROS)
            bytes.forEach { byte ->
                IrBits.sendLsbFirst(builder, byte, 8, UNIT_MICROS, ZERO_SPACE_MICROS, ONE_SPACE_MICROS)
            }
            builder.mark(UNIT_MICROS)
        }

        return IrEncoding(kaseikyo.frequencyHz, builder.build())
    }

    /** Reparte la dirección (32 bits) y el comando (10 bits) en los 6 bytes de la trama. */
    private fun toDataBytes(address: Int, command: Int): IntArray {
        val id = (address ushr 24) and 0x03
        val vendorId = (address ushr 8) and 0xFFFF
        val genre1 = (address ushr 4) and 0x0F
        val genre2 = address and 0x0F

        val vendorLow = vendorId and 0xFF
        val vendorHigh = (vendorId ushr 8) and 0xFF
        val vendorXor = vendorLow xor vendorHigh
        val vendorParity = (vendorXor and 0x0F) xor ((vendorXor ushr 4) and 0x0F)

        val first = vendorParity or (genre1 shl 4)
        val second = (genre2 and 0x0F) or ((command and 0x0F) shl 4)
        val third = (id shl 6) or ((command ushr 4) and 0x3F)
        val parity = first xor second xor third

        return intArrayOf(vendorLow, vendorHigh, first, second, third, parity)
    }

    companion object {
        const val UNIT_MICROS = 432
        const val PREAMBLE_MARK_MICROS = 8 * UNIT_MICROS
        const val PREAMBLE_SPACE_MICROS = 4 * UNIT_MICROS
        const val ZERO_SPACE_MICROS = UNIT_MICROS
        const val ONE_SPACE_MICROS = 3 * UNIT_MICROS
        const val FRAME_GAP_MICROS = 130_000
        const val MAX_REPEATS = 10
    }
}
