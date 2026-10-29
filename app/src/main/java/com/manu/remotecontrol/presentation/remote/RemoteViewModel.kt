package com.manu.remotecontrol.presentation.remote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manu.remotecontrol.domain.model.AppSettings
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.IrHardwareInfo
import com.manu.remotecontrol.domain.model.IrTransmissionResult
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.domain.model.RemoteDevice
import com.manu.remotecontrol.domain.usecase.DeleteDeviceUseCase
import com.manu.remotecontrol.domain.usecase.GetIrHardwareUseCase
import com.manu.remotecontrol.domain.usecase.ObserveButtonsUseCase
import com.manu.remotecontrol.domain.usecase.ObserveDeviceUseCase
import com.manu.remotecontrol.domain.usecase.ObserveSettingsUseCase
import com.manu.remotecontrol.domain.usecase.SendIrCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TEMPLATE_ROLES: Set<ButtonRole> = setOf(
        ButtonRole.POWER,
        ButtonRole.POWER_ON,
        ButtonRole.POWER_OFF,
        ButtonRole.UP,
        ButtonRole.DOWN,
        ButtonRole.LEFT,
        ButtonRole.RIGHT,
        ButtonRole.OK,
        ButtonRole.VOL_UP,
        ButtonRole.VOL_DOWN,
        ButtonRole.MUTE,
        ButtonRole.CH_UP,
        ButtonRole.CH_DOWN,
        ButtonRole.DIGIT_0,
        ButtonRole.DIGIT_1,
        ButtonRole.DIGIT_2,
        ButtonRole.DIGIT_3,
        ButtonRole.DIGIT_4,
        ButtonRole.DIGIT_5,
        ButtonRole.DIGIT_6,
        ButtonRole.DIGIT_7,
        ButtonRole.DIGIT_8,
        ButtonRole.DIGIT_9,
        ButtonRole.PLAY,
        ButtonRole.PAUSE,
        ButtonRole.STOP,
        ButtonRole.NEXT,
        ButtonRole.PREVIOUS,
        ButtonRole.REWIND,
        ButtonRole.FORWARD,
        ButtonRole.RECORD,
        ButtonRole.TEMP_UP,
        ButtonRole.TEMP_DOWN,
        ButtonRole.MODE,
        ButtonRole.FAN,
        ButtonRole.SWING
    )


data class RemoteUiState(
    val device: RemoteDevice? = null,
    val buttons: List<RemoteButton> = emptyList(),
    val hardware: IrHardwareInfo = IrHardwareInfo.Unavailable,
    val settings: AppSettings = AppSettings(),
    val temperature: Int = 24,
    val lastResult: IrTransmissionResult? = null,
    val pulse: Long = 0L,
    val isLoading: Boolean = true,
    val deleted: Boolean = false
) {
    val simulationActive: Boolean
        get() = settings.forceSimulationMode || !hardware.isIrAvailable

    fun button(role: ButtonRole): RemoteButton? = buttons.firstOrNull { it.role == role }

    fun buttonsWithRole(role: ButtonRole): List<RemoteButton> = buttons.filter { it.role == role }

    /** Botones que no forman parte de las plantillas estándar y se muestran como extras. */
    val extraButtons: List<RemoteButton>
        get() = buttons.filter { it.role !in TEMPLATE_ROLES }
}

private data class TransientState(
    val temperature: Int = 24,
    val lastResult: IrTransmissionResult? = null,
    val pulse: Long = 0L,
    val deleted: Boolean = false
)

@HiltViewModel
class RemoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeDevice: ObserveDeviceUseCase,
    observeButtons: ObserveButtonsUseCase,
    observeSettings: ObserveSettingsUseCase,
    private val getIrHardware: GetIrHardwareUseCase,
    private val sendIrCode: SendIrCodeUseCase,
    private val deleteDevice: DeleteDeviceUseCase
) : ViewModel() {

    private val deviceId: Long = savedStateHandle.get<Long>("deviceId") ?: 0L
    private val transient = MutableStateFlow(TransientState())
    private val hardware = MutableStateFlow(IrHardwareInfo.Unavailable)

    val uiState: StateFlow<RemoteUiState> = combine(
        observeDevice(deviceId),
        observeButtons(deviceId),
        observeSettings(),
        hardware,
        transient
    ) { device, buttons, settings, hardwareInfo, extra ->
        RemoteUiState(
            device = device,
            buttons = buttons,
            hardware = hardwareInfo,
            settings = settings,
            temperature = extra.temperature,
            lastResult = extra.lastResult,
            pulse = extra.pulse,
            isLoading = false,
            deleted = extra.deleted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RemoteUiState()
    )

    init {
        viewModelScope.launch { hardware.value = getIrHardware() }
    }

    fun onButtonPress(button: RemoteButton) {
        viewModelScope.launch {
            val result = sendIrCode(button.code, buttonLabel = button.label)
            transient.update {
                it.copy(lastResult = result, pulse = it.pulse + 1)
            }
        }
    }

    /** Ajusta la temperatura (16–30 °C) y envía TEMP+/TEMP− si el mando los tiene. */
    fun onTemperatureChange(delta: Int) {
        val state = uiState.value
        val next = (state.temperature + delta).coerceIn(MIN_TEMPERATURE, MAX_TEMPERATURE)
        transient.update { it.copy(temperature = next) }
        val role = if (delta > 0) ButtonRole.TEMP_UP else ButtonRole.TEMP_DOWN
        state.button(role)?.let { onButtonPress(it) }
    }

    fun onDeleteDevice() {
        viewModelScope.launch {
            deleteDevice(deviceId)
            transient.update { it.copy(deleted = true) }
        }
    }

    fun clearMessage() {
        transient.update { it.copy(lastResult = null) }
    }

    companion object {
        const val MIN_TEMPERATURE = 16
        const val MAX_TEMPERATURE = 30
    }
}
