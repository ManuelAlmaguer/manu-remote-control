package com.manu.remotecontrol.domain.ir

/**
 * Representación de dominio de una señal infrarroja ya decodificada.
 *
 * Cada variante guarda los parámetros lógicos del protocolo (dirección, comando, bits...)
 * y no los tiempos: el [IrEncoder] correspondiente se encarga de convertirla en una
 * secuencia de marcas y espacios en microsegundos lista para `ConsumerIrManager`.
 */
sealed interface IrCode {

    /** Protocolo lógico de la señal. */
    val protocol: IrProtocolId

    /** Frecuencia de portadora en Hz (38 kHz, 40 kHz, 36 kHz...). */
    val frequencyHz: Int

    /** Número de repeticiones de la trama completa (mínimo 1). */
    val repeats: Int

    /**
     * NEC clásico (32 bits: address + ~address + command + ~command).
     *
     * @param address dirección de 8 bits del dispositivo.
     * @param command comando de 8 bits.
     * @param extended si es `true` se emite NEC extendido: dirección de 16 bits
     *   sin invertir + comando + ~comando (usado por TCL, Hisense, muchos aires LG).
     */
    data class Nec(
        val address: Int,
        val command: Int,
        override val frequencyHz: Int = 38_000,
        override val repeats: Int = 1,
        val extended: Boolean = false
    ) : IrCode {
        override val protocol: IrProtocolId
            get() = if (extended) IrProtocolId.NEC_EXTENDED else IrProtocolId.NEC
    }

    /** Samsung 32 bits: address + address + command + ~command. */
    data class Samsung(
        val address: Int,
        val command: Int,
        override val frequencyHz: Int = 38_000,
        override val repeats: Int = 1
    ) : IrCode {
        override val protocol: IrProtocolId get() = IrProtocolId.SAMSUNG
    }

    /**
     * Sony SIRC. Los bits de comando (7) se envían primero en LSB, luego la dirección.
     *
     * @param device dirección del equipo (5 bits en SIRC12, 8 bits en SIRC15 y en la
     *   parte baja de SIRC20).
     * @param command comando de 7 bits.
     * @param bits longitud de trama: 12, 15 o 20.
     */
    data class Sony(
        val device: Int,
        val command: Int,
        val bits: Int = 12,
        override val frequencyHz: Int = 40_000,
        override val repeats: Int = 2
    ) : IrCode {
        override val protocol: IrProtocolId get() = IrProtocolId.SONY_SIRC
    }

    /**
     * Philips RC-5 (14 bits, codificación Manchester, 36 kHz).
     *
     * @param system dirección de sistema de 5 bits (0–31).
     * @param command comando de 6 bits (0–63).
     * @param toggle bit de alternancia RC-5.
     */
    data class Rc5(
        val system: Int,
        val command: Int,
        val toggle: Boolean = false,
        override val frequencyHz: Int = 36_000,
        override val repeats: Int = 1
    ) : IrCode {
        override val protocol: IrProtocolId get() = IrProtocolId.RC5
    }

    /**
     * Patrón crudo en microsegundos (pares marca/espacio alternos empezando por marca).
     * Sirve para códigos aprendidos, Pronto convertido o aires acondicionados complejos.
     */
    data class Raw(
        val pattern: List<Int>,
        override val frequencyHz: Int = 38_000,
        override val repeats: Int = 1
    ) : IrCode {
        override val protocol: IrProtocolId get() = IrProtocolId.RAW
    }
}

/** Longitud aproximada de la señal en microsegundos (útil para diagnósticos y UI). */
fun IrCode.estimatedDurationMicros(): Long = when (this) {
    is IrCode.Raw -> pattern.sumOf { it.toLong() } * repeats.coerceAtLeast(1)
    else -> 0L
}
