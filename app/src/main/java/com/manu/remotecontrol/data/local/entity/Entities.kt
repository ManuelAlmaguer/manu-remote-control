package com.manu.remotecontrol.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Dispositivo guardado por el usuario. */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val brand: String,
    val model: String,
    val type: String,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val carrierFrequencyHz: Int = 38_000,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Botón de un control. Guarda el código IR de forma normalizada: protocolo + dirección +
 * comando (o patrón RAW) para poder reconstruirlo sin depender de ninguna librería externa.
 */
@Entity(
    tableName = "buttons",
    foreignKeys = [
        ForeignKey(
            entity = DeviceEntity::class,
            parentColumns = ["id"],
            childColumns = ["deviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deviceId")]
)
data class ButtonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val deviceId: Long,
    val label: String,
    val role: String,
    val section: String,
    /** Nombre del enum [com.manu.remotecontrol.domain.ir.IrProtocolId]. */
    val protocol: String,
    val frequencyHz: Int,
    val address: Int = 0,
    val command: Int = 0,
    /** Bits de trama en Sony SIRC (12/15/20). */
    val bits: Int = 12,
    /** Bit de alternancia RC-5. */
    val toggle: Boolean = false,
    val repeats: Int = 1,
    /** Patrón RAW separado por espacios, solo para `IrProtocolId.RAW`. */
    val rawPattern: String? = null,
    val sortOrder: Int = 0
)

/** Dispositivo del catálogo precargado (semilla offline). */
@Entity(tableName = "catalog_devices")
data class CatalogDeviceEntity(
    @PrimaryKey val catalogKey: String,
    val name: String,
    val brand: String,
    val model: String,
    val type: String,
    val frequencyHz: Int,
    val description: String,
    val source: String,
    val buttonCount: Int
)

/** Botones del catálogo precargado. */
@Entity(
    tableName = "catalog_buttons",
    foreignKeys = [
        ForeignKey(
            entity = CatalogDeviceEntity::class,
            parentColumns = ["catalogKey"],
            childColumns = ["catalogKey"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("catalogKey")]
)
data class CatalogButtonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val catalogKey: String,
    val label: String,
    val role: String,
    val section: String,
    val protocol: String,
    val frequencyHz: Int,
    val address: Int = 0,
    val command: Int = 0,
    val bits: Int = 12,
    val toggle: Boolean = false,
    val repeats: Int = 1,
    val rawPattern: String? = null,
    val sortOrder: Int = 0
)

/** Proyección de dispositivo con su número de botones. */
data class DeviceWithButtonCount(
    @Embedded val device: DeviceEntity,
    @ColumnInfo(name = "buttonCount") val buttonCount: Int
)

/** Proyección de catálogo con número de botones. */
data class CatalogWithCount(
    @Embedded val catalog: CatalogDeviceEntity
)
