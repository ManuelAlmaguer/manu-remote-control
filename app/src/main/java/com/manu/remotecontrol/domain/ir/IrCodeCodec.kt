package com.manu.remotecontrol.domain.ir

import com.manu.remotecontrol.domain.ir.protocols.RawEncoder

/**
 * Conversión entre los códigos IR lógicos y la representación HEX que el usuario ve y
 * escribe en el editor de botones personalizados.
 *
 * Formatos:
 * - NEC / NEC extendido / Samsung: 8 dígitos hexadecimales (4 bytes, MSB primero).
 * - Sony SIRC: 4 dígitos (device + command de 7 bits).
 * - RC-5: 4 dígitos (sistema de 5 bits + comando de 6 bits).
 * - RAW: lista de microsegundos separada por espacios.
 */
object IrCodeCodec {

    private const val HEX_RADIX = 16

    fun toHex(code: IrCode): String = when (code) {
        is IrCode.Nec -> {
            val addressLow: Int
            val addressHigh: Int
            if (code.extended) {
                addressLow = code.address and 0xFF
                addressHigh = (code.address shr 8) and 0xFF
            } else {
                addressLow = code.address and 0xFF
                addressHigh = code.address.inv() and 0xFF
            }
            "%02X%02X%02X%02X".format(addressLow, addressHigh, code.command and 0xFF, code.command.inv() and 0xFF)
        }

        is IrCode.Samsung -> "%02X%02X%02X%02X".format(
            code.address and 0xFF,
            code.address and 0xFF,
            code.command and 0xFF,
            code.command.inv() and 0xFF
        )

        is IrCode.Sony -> "%02X%02X".format(code.device and 0xFF, code.command and 0x7F)

        is IrCode.Rc5 -> "%02X%02X".format(code.system and 0x1F, code.command and 0x3F)

        is IrCode.Raw -> code.pattern.joinToString(" ")
    }

    /**
     * Interpreta un HEX escrito por el usuario y construye el [IrCode] correspondiente.
     * Devuelve `null` si el formato no es válido para el protocolo indicado.
     */
    fun parse(
        protocol: IrProtocolId,
        hexInput: String,
        frequencyHz: Int,
        repeats: Int = 1,
        toggle: Boolean = false
    ): IrCode? {
        val cleaned = hexInput.trim()
            .removePrefix("0x")
            .removePrefix("0X")
            .replace(" ", "")
            .replace(":", "")
        if (cleaned.isEmpty()) return null

        return when (protocol) {
            IrProtocolId.NEC, IrProtocolId.NEC_EXTENDED, IrProtocolId.SAMSUNG -> {
                if (cleaned.length != 8 || cleaned.any { it.digitToIntOrNull(HEX_RADIX) == null }) return null
                val bytes = (0 until 4).map { cleaned.substring(it * 2, it * 2 + 2).toInt(HEX_RADIX) }
                when (protocol) {
                    IrProtocolId.NEC -> IrCode.Nec(
                        address = bytes[0],
                        command = bytes[2],
                        frequencyHz = frequencyHz,
                        repeats = repeats
                    )

                    IrProtocolId.NEC_EXTENDED -> IrCode.Nec(
                        address = bytes[0] or (bytes[1] shl 8),
                        command = bytes[2],
                        frequencyHz = frequencyHz,
                        repeats = repeats,
                        extended = true
                    )

                    else -> IrCode.Samsung(
                        address = bytes[0],
                        command = bytes[2],
                        frequencyHz = frequencyHz,
                        repeats = repeats
                    )
                }
            }

            IrProtocolId.SONY_SIRC -> {
                if (cleaned.length != 4 || cleaned.any { it.digitToIntOrNull(HEX_RADIX) == null }) return null
                val device = cleaned.substring(0, 2).toInt(HEX_RADIX)
                val command = cleaned.substring(2, 4).toInt(HEX_RADIX) and 0x7F
                IrCode.Sony(
                    device = device,
                    command = command,
                    bits = if (device > 0x1F) 15 else 12,
                    frequencyHz = frequencyHz,
                    repeats = if (repeats < 2) 2 else repeats
                )
            }

            IrProtocolId.RC5 -> {
                if (cleaned.length != 4 || cleaned.any { it.digitToIntOrNull(HEX_RADIX) == null }) return null
                IrCode.Rc5(
                    system = cleaned.substring(0, 2).toInt(HEX_RADIX) and 0x1F,
                    command = cleaned.substring(2, 4).toInt(HEX_RADIX) and 0x3F,
                    toggle = toggle,
                    frequencyHz = frequencyHz,
                    repeats = repeats
                )
            }

            IrProtocolId.RAW -> {
                val pattern = RawEncoder.parsePattern(hexInput) ?: return null
                IrCode.Raw(pattern = pattern, frequencyHz = frequencyHz, repeats = repeats)
            }
        }
    }

    /** Etiqueta corta para mostrar en listas y chips: "NEC · 04FB08F7". */
    fun shortLabel(code: IrCode): String = "${code.protocol.displayName} · ${toHex(code).take(24)}"
}
