package com.manu.remotecontrol.domain.model

import com.manu.remotecontrol.domain.ir.IrCode

/**
 * Dispositivo guardado por el usuario (o importado del catálogo offline).
 */
data class RemoteDevice(
    val id: Long = 0L,
    val name: String,
    val brand: String,
    val model: String,
    val type: DeviceType,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val carrierFrequencyHz: Int = 38_000,
    val createdAt: Long = System.currentTimeMillis(),
    val buttonCount: Int = 0
) {
    val displaySubtitle: String
        get() = listOf(brand, model).filter { it.isNotBlank() }.joinToString(" · ")
}

/**
 * Botón del control remoto, ligado a un dispositivo y a un [IrCode].
 */
data class RemoteButton(
    val id: Long = 0L,
    val deviceId: Long,
    val label: String,
    val role: ButtonRole,
    val section: ButtonSection,
    val code: IrCode,
    val sortOrder: Int = 0
)

/**
 * Entrada del catálogo precargado (semilla offline tipo IRDB / Flipper-IRDB).
 */
data class CatalogDevice(
    val key: String,
    val name: String,
    val brand: String,
    val model: String,
    val type: DeviceType,
    val frequencyHz: Int,
    val description: String,
    val buttonCount: Int = 0,
    val source: String = "Flipper-IRDB (CC0)"
) {
    val displaySubtitle: String
        get() = listOf(brand, model, description).filter { it.isNotBlank() }.joinToString(" · ")
}
