package com.manu.remotecontrol.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manu.remotecontrol.BuildConfig
import com.manu.remotecontrol.domain.model.AppSettings
import com.manu.remotecontrol.domain.model.IrHardwareInfo
import com.manu.remotecontrol.domain.model.ThemeMode
import com.manu.remotecontrol.domain.usecase.GetIrHardwareUseCase
import com.manu.remotecontrol.domain.usecase.ObserveSettingsUseCase
import com.manu.remotecontrol.domain.usecase.UpdateCarrierFrequencyUseCase
import com.manu.remotecontrol.domain.usecase.UpdateDynamicColorUseCase
import com.manu.remotecontrol.domain.usecase.UpdateHapticsUseCase
import com.manu.remotecontrol.domain.usecase.UpdateSimulationModeUseCase
import com.manu.remotecontrol.domain.usecase.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val hardware: IrHardwareInfo = IrHardwareInfo.Unavailable,
    val appVersion: String = BuildConfig.VERSION_NAME
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase,
    private val getIrHardware: GetIrHardwareUseCase,
    private val updateTheme: UpdateThemeUseCase,
    private val updateDynamicColor: UpdateDynamicColorUseCase,
    private val updateCarrier: UpdateCarrierFrequencyUseCase,
    private val updateHaptics: UpdateHapticsUseCase,
    private val updateSimulation: UpdateSimulationModeUseCase
) : ViewModel() {

    private val hardware = MutableStateFlow(IrHardwareInfo.Unavailable)

    val uiState: StateFlow<SettingsUiState> = combine(
        observeSettings(),
        hardware
    ) { settings, hardwareInfo ->
        SettingsUiState(
            settings = settings,
            hardware = hardwareInfo,
            appVersion = BuildConfig.VERSION_NAME
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    init {
        refreshHardware()
    }

    fun refreshHardware() {
        viewModelScope.launch { hardware.value = getIrHardware() }
    }

    fun setTheme(mode: ThemeMode) = viewModelScope.launch { updateTheme(mode) }

    fun setDynamicColor(enabled: Boolean) = viewModelScope.launch { updateDynamicColor(enabled) }

    fun setCarrier(hz: Int?) = viewModelScope.launch { updateCarrier(hz) }

    fun setHaptics(enabled: Boolean) = viewModelScope.launch { updateHaptics(enabled) }

    fun setSimulation(enabled: Boolean) = viewModelScope.launch { updateSimulation(enabled) }
}
