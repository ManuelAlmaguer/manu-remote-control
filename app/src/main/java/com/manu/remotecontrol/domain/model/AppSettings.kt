package com.manu.remotecontrol.domain.model

/** Preferencia de tema de la app. */
enum class ThemeMode(val storageKey: String, val displayName: String) {
    SYSTEM("SYSTEM", "Automático (sistema)"),
    LIGHT("LIGHT", "Claro"),
    DARK("DARK", "Oscuro");

    companion object {
        fun fromKey(key: String?): ThemeMode =
            entries.firstOrNull { it.storageKey == key } ?: SYSTEM
    }
}

/**
 * Ajustes de la app persistidos con DataStore.
 *
 * @property carrierFrequencyOverrideHz frecuencia de portadora manual (calibración).
 *   `null` significa "usar la frecuencia del protocolo".
 * @property forceSimulationMode activa el Modo Simulación aunque el teléfono tenga IR.
 * @property hapticsEnabled vibración háptica al pulsar botones.
 */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val carrierFrequencyOverrideHz: Int? = null,
    val hapticsEnabled: Boolean = true,
    val forceSimulationMode: Boolean = false,
    val showSignalDetails: Boolean = false
) {
    companion object {
        const val MIN_CARRIER_HZ = 30_000
        const val MAX_CARRIER_HZ = 60_000
        const val DEFAULT_CARRIER_HZ = 38_000
    }
}
