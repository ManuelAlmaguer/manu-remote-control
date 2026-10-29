package com.manu.remotecontrol.domain.ir

/**
 * Resultado del proceso de codificación: la frecuencia de portadora definitiva y el
 * patrón de microsegundos alternando encendido/apagado (empezando siempre por encendido),
 * que es exactamente lo que espera `ConsumerIrManager.transmit(frequency, pattern)`.
 */
class IrEncoding(
    val frequencyHz: Int,
    val pattern: IntArray
) {
    /** Duración total de la emisión en microsegundos. */
    val durationMicros: Long = pattern.sumOf { it.toLong() }

    /** Número de pares marca/espacio. */
    val pulseCount: Int = pattern.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IrEncoding) return false
        return frequencyHz == other.frequencyHz && pattern.contentEquals(other.pattern)
    }

    override fun hashCode(): Int = 31 * frequencyHz + pattern.contentHashCode()

    override fun toString(): String =
        "IrEncoding(freq=$frequencyHz Hz, pulses=${pattern.size}, duration=${durationMicros}us)"

    companion object {
        fun of(frequencyHz: Int, pattern: List<Int>): IrEncoding =
            IrEncoding(frequencyHz, pattern.toIntArray())
    }
}

/** Contrato que implementa cada protocolo del motor IR. */
interface IrEncoder {

    /** Protocolo principal que resuelve este codificador. */
    val protocol: IrProtocolId

    /**
     * Protocolos adicionales que comparten la misma implementación
     * (por ejemplo NEC y NEC extendido).
     */
    val aliases: List<IrProtocolId>
        get() = emptyList()

    /** Indica si este codificador sabe tratar el código recibido. */
    fun supports(code: IrCode): Boolean

    /**
     * Convierte un [IrCode] en un [IrEncoding].
     * @throws IllegalArgumentException si el código no es válido para el protocolo.
     */
    fun encode(code: IrCode): IrEncoding
}

/**
 * Registro de protocolos: punto único de extensión del motor IR.
 *
 * Para añadir un protocolo nuevo (por ejemplo RC-6 o Kaseikyo) basta con crear una clase
 * que implemente [IrEncoder] y añadirla a la lista que se inyecta aquí. Nada más cambia.
 */
class IrProtocolRegistry(encoders: List<IrEncoder>) {

    private val encodersByProtocol: Map<IrProtocolId, IrEncoder> = buildMap {
        encoders.forEach { encoder ->
            put(encoder.protocol, encoder)
            encoder.aliases.forEach { alias -> put(alias, encoder) }
        }
    }

    init {
        val declared = encoders.sumOf { 1 + it.aliases.size }
        require(encodersByProtocol.size == declared) {
            "Hay protocolos duplicados en el registro: ${encoders.map { listOf(it.protocol) + it.aliases }}"
        }
    }

    val supportedProtocols: List<IrProtocolId>
        get() = IrProtocolId.entries.filter { it in encodersByProtocol }

    fun encoderFor(protocol: IrProtocolId): IrEncoder =
        encodersByProtocol[protocol] ?: error("Protocolo no registrado: $protocol")

    fun supports(code: IrCode): Boolean = encodersByProtocol[code.protocol]?.supports(code) == true

    /** Codifica un código IR con la frecuencia de portadora indicada (o la del propio código). */
    fun encode(code: IrCode, carrierFrequencyHz: Int? = null): IrEncoding {
        val encoder = encoderFor(code.protocol)
        val encoding = encoder.encode(code)
        val frequency = carrierFrequencyHz?.takeIf { it > 0 } ?: encoding.frequencyHz
        return IrEncoding(frequency, encoding.pattern)
    }
}
