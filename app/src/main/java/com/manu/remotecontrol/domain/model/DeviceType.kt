package com.manu.remotecontrol.domain.model

/**
 * Tipos de dispositivo que la app sabe representar.
 *
 * El tipo determina el diseño de la pantalla de control (un televisor usa D-Pad y teclado
 * numérico; un aire acondicionado usa temperatura, modo y ventilador).
 */
enum class DeviceType(val storageKey: String, val displayName: String, val emoji: String) {
    TV("TV", "Televisor", "📺"),
    AC("AC", "Aire acondicionado", "❄️"),
    DVD("DVD", "DVD / Blu-ray", "📀"),
    AUDIO("AUDIO", "Audio / Soundbar", "🔊"),
    PROJECTOR("PROJECTOR", "Proyector", "📽️"),
    FAN("FAN", "Ventilador", "🌀"),
    LIGHT("LIGHT", "Iluminación", "💡"),
    OTHER("OTHER", "Otro", "🎛️");

    companion object {
        fun fromKey(key: String?): DeviceType =
            entries.firstOrNull { it.storageKey.equals(key, ignoreCase = true) } ?: OTHER
    }
}
