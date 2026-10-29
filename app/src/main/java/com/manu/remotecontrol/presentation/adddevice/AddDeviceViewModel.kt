package com.manu.remotecontrol.presentation.adddevice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manu.remotecontrol.domain.model.CatalogDevice
import com.manu.remotecontrol.domain.model.DeviceType
import com.manu.remotecontrol.domain.model.IrTransmissionResult
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.domain.model.RemoteDevice
import com.manu.remotecontrol.domain.usecase.AddCatalogDeviceUseCase
import com.manu.remotecontrol.domain.usecase.SaveDeviceUseCase
import com.manu.remotecontrol.domain.usecase.SearchCatalogUseCase
import com.manu.remotecontrol.domain.usecase.SendIrCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AddDeviceStep {
    TYPE,
    BRAND,
    MODEL,
    TEST
}

data class AddDeviceUiState(
    val step: AddDeviceStep = AddDeviceStep.TYPE,
    val selectedType: DeviceType? = null,
    val selectedBrand: String? = null,
    val selectedCatalog: CatalogDevice? = null,
    val brands: List<String> = emptyList(),
    val devices: List<CatalogDevice> = emptyList(),
    val previewButtons: List<RemoteButton> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val lastTest: IrTransmissionResult? = null,
    val createdDeviceId: Long? = null,
    val isCustomCreation: Boolean = false,
    val error: String? = null
) {
    val canGoBack: Boolean get() = step != AddDeviceStep.TYPE
}

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val searchCatalog: SearchCatalogUseCase,
    private val addCatalogDevice: AddCatalogDeviceUseCase,
    private val saveDevice: SaveDeviceUseCase,
    private val sendIrCode: SendIrCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDeviceUiState())
    val uiState: StateFlow<AddDeviceUiState> = _uiState.asStateFlow()

    fun selectType(type: DeviceType) {
        _uiState.update {
            it.copy(
                step = AddDeviceStep.BRAND,
                selectedType = type,
                selectedBrand = null,
                selectedCatalog = null,
                query = ""
            )
        }
        loadBrandsAndDevices()
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }
        loadDevices()
    }

    fun selectBrand(brand: String) {
        _uiState.update { it.copy(step = AddDeviceStep.MODEL, selectedBrand = brand, query = "") }
        loadDevices()
    }

    fun selectDevice(device: CatalogDevice) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedCatalog = device, isLoading = true) }
            val buttons = searchCatalog.buttons(device.key)
            _uiState.update {
                it.copy(
                    step = AddDeviceStep.TEST,
                    previewButtons = buttons,
                    isLoading = false
                )
            }
        }
    }

    fun testButton(button: RemoteButton) {
        viewModelScope.launch {
            val result = sendIrCode(button.code, buttonLabel = button.label)
            _uiState.update { it.copy(lastTest = result) }
        }
    }

    fun saveSelected() {
        val catalog = _uiState.value.selectedCatalog ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val id = addCatalogDevice(catalog.key)
            if (id != null) {
                _uiState.update { it.copy(isSaving = false, createdDeviceId = id) }
            } else {
                _uiState.update {
                    it.copy(isSaving = false, error = "No se pudo guardar el dispositivo")
                }
            }
        }
    }

    /** Crea un control vacío para configurarlo botón a botón desde el editor. */
    fun createCustomDevice(name: String, brand: String, model: String, type: DeviceType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val id = saveDevice(
                RemoteDevice(
                    name = name.ifBlank { "Mi control" },
                    brand = brand,
                    model = model,
                    type = type,
                    isCustom = true,
                    carrierFrequencyHz = 38_000
                )
            )
            _uiState.update { it.copy(isSaving = false, createdDeviceId = id, isCustomCreation = true) }
        }
    }

    fun goBack(): Boolean {
        val state = _uiState.value
        val previous = when (state.step) {
            AddDeviceStep.TYPE -> return false
            AddDeviceStep.BRAND -> AddDeviceStep.TYPE
            AddDeviceStep.MODEL -> AddDeviceStep.BRAND
            AddDeviceStep.TEST -> AddDeviceStep.MODEL
        }
        _uiState.update { it.copy(step = previous, lastTest = null) }
        if (previous == AddDeviceStep.BRAND) loadBrandsAndDevices()
        if (previous == AddDeviceStep.MODEL) loadDevices()
        return true
    }

    fun clearError() = _uiState.update { it.copy(error = null) }

    private fun loadBrandsAndDevices() {
        val type = _uiState.value.selectedType ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val brands = searchCatalog.brands(type)
            val devices = searchCatalog("", type)
            _uiState.update { it.copy(brands = brands, devices = devices, isLoading = false) }
        }
    }

    private fun loadDevices() {
        val type = _uiState.value.selectedType ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val devices = searchCatalog(_uiState.value.query, type)
            val brand = _uiState.value.selectedBrand
            val filtered = if (brand.isNullOrBlank()) devices else devices.filter { it.brand == brand }
            _uiState.update { it.copy(devices = filtered, isLoading = false) }
        }
    }
}
