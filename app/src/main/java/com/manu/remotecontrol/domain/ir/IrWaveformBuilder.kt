package com.manu.remotecontrol.domain.ir

/**
 * Constructor de formas de onda IR.
 *
 * Los protocolos añaden marcas (portadora encendida) y espacios (portadora apagada) sin
 * preocuparse por el formato final. El builder fusiona segmentos consecutivos del mismo
 * tipo (necesario para protocolos Manchester como RC-5) y garantiza que el patrón final
 * empieza siempre con una marca, tal como exige `ConsumerIrManager.transmit`.
 */
class IrWaveformBuilder {

    /** Duraciones con signo: positivo = marca, negativo = espacio. */
    private val segments = mutableListOf<Int>()

    fun mark(micros: Int) = add(kotlin.math.abs(micros))

    fun space(micros: Int) = add(-kotlin.math.abs(micros))

    /** Añade una marca y un espacio consecutivos. */
    fun pair(markMicros: Int, spaceMicros: Int) {
        mark(markMicros)
        space(spaceMicros)
    }

    private fun add(signedMicros: Int) {
        if (signedMicros == 0) return
        val last = segments.lastOrNull()
        if (last != null && (last > 0) == (signedMicros > 0)) {
            segments[segments.lastIndex] = last + signedMicros
        } else {
            segments.add(signedMicros)
        }
    }

    /** Patrón final: valores absolutos alternos, empezando por marca. */
    fun build(): IntArray {
        val cleaned = segments.toMutableList()
        while (cleaned.isNotEmpty() && cleaned.first() < 0) cleaned.removeAt(0)
        if (cleaned.isEmpty()) return IntArray(0)
        return IntArray(cleaned.size) { kotlin.math.abs(cleaned[it]) }
    }
}

/** Utilidades compartidas por los codificadores. */
internal object IrBits {

    /** Envía [bitCount] bits en formato pulse-distance, empezando por el bit menos significativo. */
    fun sendLsbFirst(
        builder: IrWaveformBuilder,
        value: Int,
        bitCount: Int,
        markMicros: Int,
        zeroSpaceMicros: Int,
        oneSpaceMicros: Int
    ) {
        for (i in 0 until bitCount) {
            val bit = (value shr i) and 1
            builder.mark(markMicros)
            builder.space(if (bit == 1) oneSpaceMicros else zeroSpaceMicros)
        }
    }

    /** Envía [bitCount] bits en formato pulse-distance, empezando por el bit más significativo. */
    fun sendMsbFirst(
        builder: IrWaveformBuilder,
        value: Int,
        bitCount: Int,
        markMicros: Int,
        zeroSpaceMicros: Int,
        oneSpaceMicros: Int
    ) {
        for (i in bitCount - 1 downTo 0) {
            val bit = (value shr i) and 1
            builder.mark(markMicros)
            builder.space(if (bit == 1) oneSpaceMicros else zeroSpaceMicros)
        }
    }

    /**
     * Codificación Manchester (bi-fase) usada por RC-5: un bit `1` es espacio+marca y un
     * bit `0` es marca+espacio, con medias duraciones de [halfBitMicros].
     */
    fun sendManchester(builder: IrWaveformBuilder, bit: Int, halfBitMicros: Int) {
        if (bit == 1) {
            builder.space(halfBitMicros)
            builder.mark(halfBitMicros)
        } else {
            builder.mark(halfBitMicros)
            builder.space(halfBitMicros)
        }
    }
}
