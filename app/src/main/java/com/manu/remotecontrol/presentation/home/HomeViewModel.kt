package com.manu.remotecontrol.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manu.remotecontrol.domain.model.AppSettings
import com.manu.remotecontrol.domain.model.IrHardwareInfo
import com.manu.remotecontrol.domain.model.RemoteDevice
import com.manu.remotecontrol.domain.usecase.DeleteDeviceUseCase
import com.manu.remotecontrol.domain.usecase.GetIrHardwareUseCase
import com.manu.remotecontrol.domain.usecase.ObserveDevicesUseCase
import com.manu.remotecontrol.domain.usecase.ObserveSettingsUseCase
import com.manu.remotecontrol.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val devices: List<RemoteDevice> = emptyList(),
    val hardware: IrHardwareInfo = IrHardwareInfo.Unavailable,
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = true
) {
    val simulationActive: Boolean
        get() = settings.forceSimulationMode || !hardware.isIrAvailable
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeDevices: ObserveDevicesUseCase,
    observeSettings: ObserveSettingsUseCase,
    private val getIrHardware: GetIrHardwareUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val deleteDevice: DeleteDeviceUseCase
) : ViewModel() {

    private val hardware = MutableStateFlow(IrHardwareInfo.Unavailable)
    private val loading = MutableStateFlow(true)

    val uiState: StateFlow<HomeUiState> = combine(
        observeDevices(),
        observeSettings(),
        hardware,
        loading
    ) { devices, settings, hardwareInfo, isLoading ->
        HomeUiState(
            devices = devices,
            hardware = hardwareInfo,
            settings = settings,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    init {
        refreshHardware()
    }

    fun refreshHardware() {
        viewModelScope.launch {
            hardware.value = getIrHardware()
            loading.value = false
        }
    }

    fun onFavoriteToggle(device: RemoteDevice) {
        viewModelScope.launch { toggleFavorite(device.id, !device.isFavorite) }
    }

    fun onDelete(device: RemoteDevice) {
        viewModelScope.launch { deleteDevice(device.id) }
    }
}
