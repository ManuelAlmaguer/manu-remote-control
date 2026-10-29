package com.manu.remotecontrol.domain.usecase

import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.model.AppSettings
import com.manu.remotecontrol.domain.model.ButtonSection
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.CatalogDevice
import com.manu.remotecontrol.domain.model.DeviceType
import com.manu.remotecontrol.domain.model.IrTransmissionResult
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.domain.model.RemoteDevice
import com.manu.remotecontrol.domain.model.ThemeMode
import com.manu.remotecontrol.domain.repository.CatalogRepository
import com.manu.remotecontrol.domain.repository.DeviceRepository
import com.manu.remotecontrol.domain.repository.IrHardwareRepository
import com.manu.remotecontrol.domain.repository.IrTransmitter
import com.manu.remotecontrol.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Observa la lista de dispositivos guardados. */
class ObserveDevicesUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(): Flow<List<RemoteDevice>> = repository.observeDevices()
}

/** Observa un dispositivo concreto. */
class ObserveDeviceUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(deviceId: Long): Flow<RemoteDevice?> = repository.observeDevice(deviceId)
}

/** Observa los botones del control de un dispositivo. */
class ObserveButtonsUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    operator fun invoke(deviceId: Long): Flow<List<RemoteButton>> = repository.observeButtons(deviceId)
}

/** Crea o actualiza un dispositivo. */
class SaveDeviceUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(device: RemoteDevice): Long = repository.saveDevice(device)
}

/** Elimina un dispositivo y, en cascada, todos sus botones. */
class DeleteDeviceUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(deviceId: Long) = repository.deleteDevice(deviceId)
}

/** Marca o desmarca un dispositivo como favorito. */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(deviceId: Long, favorite: Boolean) =
        repository.setFavorite(deviceId, favorite)
}

/** Guarda un botón del control (alta o edición). */
class SaveButtonUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(button: RemoteButton): Long = repository.saveButton(button)
}

/** Elimina un botón del control. */
class DeleteButtonUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(buttonId: Long) = repository.deleteButton(buttonId)
}

/** Crea un dispositivo nuevo a partir de una entrada del catálogo precargado. */
class AddCatalogDeviceUseCase @Inject constructor(
    private val repository: DeviceRepository
) {
    suspend operator fun invoke(catalogKey: String, customName: String? = null): Long? =
        repository.createFromCatalog(catalogKey, customName)
}

/** Busca en el catálogo offline por marca, modelo o nombre. */
class SearchCatalogUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    suspend operator fun invoke(query: String, type: DeviceType? = null): List<CatalogDevice> =
        repository.search(query, type?.storageKey)

    suspend fun brands(type: DeviceType? = null): List<String> = repository.brands(type?.storageKey)

    suspend fun buttons(catalogKey: String): List<RemoteButton> = repository.getCatalogButtons(catalogKey)
}

/** Emite una señal IR (real o simulada) aplicando la calibración de portadora. */
class SendIrCodeUseCase @Inject constructor(
    private val transmitter: IrTransmitter
) {
    suspend operator fun invoke(
        code: IrCode,
        carrierFrequencyHz: Int? = null,
        buttonLabel: String = ""
    ): IrTransmissionResult {
        val result = transmitter.transmit(code, carrierFrequencyHz)
        return result.copy(buttonLabel = buttonLabel)
    }
}

/** Ajustes de la app. */
class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> = repository.settings
}

class UpdateThemeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}

class UpdateDynamicColorUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDynamicColor(enabled)
}

class UpdateCarrierFrequencyUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(hz: Int?) = repository.setCarrierFrequency(hz)
}

class UpdateHapticsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setHapticsEnabled(enabled)
}

class UpdateSimulationModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setForceSimulationMode(enabled)
}

/** Comprueba el hardware IR del teléfono. */
class GetIrHardwareUseCase @Inject constructor(
    private val repository: IrHardwareRepository
) {
    suspend operator fun invoke() = repository.getHardwareInfo()
    fun observe() = repository.observeHardwareInfo()
}

/** Fábrica de códigos IR a partir de lo que escribe el usuario en el editor. */
class BuildIrCodeUseCase @Inject constructor() {
    operator fun invoke(
        protocol: IrProtocolId,
        hexOrRaw: String,
        frequencyHz: Int,
        repeats: Int,
        toggle: Boolean = false
    ): IrCode? = com.manu.remotecontrol.domain.ir.IrCodeCodec.parse(
        protocol = protocol,
        hexInput = hexOrRaw,
        frequencyHz = frequencyHz,
        repeats = repeats,
        toggle = toggle
    )
}

/** Crea un botón de dominio listo para guardar. */
class CreateButtonUseCase @Inject constructor() {
    operator fun invoke(
        deviceId: Long,
        label: String,
        role: ButtonRole,
        section: ButtonSection,
        code: IrCode,
        sortOrder: Int
    ): RemoteButton = RemoteButton(
        deviceId = deviceId,
        label = label,
        role = role,
        section = section,
        code = code,
        sortOrder = sortOrder
    )
}
