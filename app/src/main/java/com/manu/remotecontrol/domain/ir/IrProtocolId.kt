package com.manu.remotecontrol.domain.ir

/**
 * Catálogo de protocolos de infrarrojos soportados por el motor de la app.
 *
 * El motor es extensible: para añadir un protocolo nuevo basta con implementar
 * [IrEncoder], registrarlo en [IrProtocolRegistry] y añadir su entrada aquí.
 *
 * @property displayName nombre legible que se muestra en la UI.
 * @property defaultFrequencyHz frecuencia de portadora recomendada por el protocolo.
 * @property supportsRaw indica si el protocolo trabaja con patrones crudos en microsegundos.
 */
enum class IrProtocolId(
    val displayName: String,
    val defaultFrequencyHz: Int,
    val supportsRaw: Boolean = false
) {
    NEC("NEC", 38_000),
    NEC_EXTENDED("NEC Extended (16 bit address)", 38_000),
    SAMSUNG("Samsung 32", 38_000),
    SONY_SIRC("Sony SIRC (12/15/20)", 40_000),
    RC5("Philips RC-5", 36_000),
    RAW("RAW (microsegundos)", 38_000, supportsRaw = true);

    companion object {
        fun fromNameOrDefault(value: String?): IrProtocolId =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: NEC
    }
}
