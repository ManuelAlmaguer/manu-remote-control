package com.manu.remotecontrol.domain.repository

import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.model.AppSettings
import com.manu.remotecontrol.domain.model.CatalogDevice
import com.manu.remotecontrol.domain.model.ThemeMode
import com.manu.remotecontrol.domain.model.IrHardwareInfo
import com.manu.remotecontrol.domain.model.IrTransmissionResult
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.domain.model.RemoteDevice
import kotlinx.coroutines.flow.Flow

/** Acceso a los dispositivos y botones guardados por el usuario (Room, offline-first). */
interface DeviceRepository {

    fun observeDevices(): Flow<List<RemoteDevice>>

    fun observeDevice(deviceId: Long): Flow<RemoteDevice?>

    fun observeButtons(deviceId: Long): Flow<List<RemoteButton>>

    suspend fun getDevice(deviceId: Long): RemoteDevice?

    suspend fun getButtons(deviceId: Long): List<RemoteButton>

    /** Crea o actualiza un dispositivo. Devuelve su id. */
    suspend fun saveDevice(device: RemoteDevice): Long

    suspend fun deleteDevice(deviceId: Long)

    suspend fun setFavorite(deviceId: Long, favorite: Boolean)

    /** Añade o actualiza un botón del control. */
    suspend fun saveButton(button: RemoteButton): Long

    suspend fun saveButtons(buttons: List<RemoteButton>)

    suspend fun deleteButton(buttonId: Long)

    /** Crea un dispositivo completo a partir de una entrada del catálogo precargado. */
    suspend fun createFromCatalog(catalogKey: String, customName: String? = null): Long?
}

/** Catálogo offline precargado (semilla IRDB / Flipper-IRDB). */
interface CatalogRepository {

    fun observeCatalog(type: String? = null): Flow<List<CatalogDevice>>

    suspend fun search(query: String, type: String? = null): List<CatalogDevice>

    suspend fun getCatalogDevice(key: String): CatalogDevice?

    suspend fun getCatalogButtons(key: String): List<RemoteButton>

    suspend fun brands(type: String? = null): List<String>
}

/** Preferencias de la app (DataStore). */
interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun update(transform: (AppSettings) -> AppSettings)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDynamicColor(enabled: Boolean)
    suspend fun setCarrierFrequency(hz: Int?)
    suspend fun setHapticsEnabled(enabled: Boolean)
    suspend fun setForceSimulationMode(enabled: Boolean)
}

/** Información del emisor IR del teléfono. */
interface IrHardwareRepository {
    suspend fun getHardwareInfo(): IrHardwareInfo
    fun observeHardwareInfo(): Flow<IrHardwareInfo>
}

/** Emisor de señales IR (real con ConsumerIrManager o simulado). */
interface IrTransmitter {
    suspend fun transmit(code: IrCode, carrierFrequencyHz: Int? = null): IrTransmissionResult
    suspend fun isHardwareAvailable(): Boolean
}
