package com.manu.remotecontrol.presentation.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrCodeCodec
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.ButtonSection
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.domain.model.RemoteDevice
import com.manu.remotecontrol.domain.usecase.BuildIrCodeUseCase
import com.manu.remotecontrol.domain.usecase.CreateButtonUseCase
import com.manu.remotecontrol.domain.usecase.DeleteButtonUseCase
import com.manu.remotecontrol.domain.usecase.ObserveButtonsUseCase
import com.manu.remotecontrol.domain.usecase.ObserveDeviceUseCase
import com.manu.remotecontrol.domain.usecase.SaveButtonUseCase
import com.manu.remotecontrol.domain.usecase.SaveDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Formulario de alta/edición de un botón. */
data class ButtonForm(
    val id: Long = 0L,
    val label: String = "",
    val role: ButtonRole = ButtonRole.CUSTOM,
    val protocol: IrProtocolId = IrProtocolId.NEC,
    val code: String = "",
    val frequencyKhz: Int = 38,
    val repeats: Int = 1,
    val toggle: Boolean = false
) {
    val isEditing: Boolean get() = id != 0L
}

data class EditorUiState(
    val device: RemoteDevice? = null,
    val buttons: List<RemoteButton> = emptyList(),
    val form: ButtonForm? = null,
    val formError: String? = null,
    val protocolOptions: List<IrProtocolId> = IrProtocolId.entries,
    val renameDialog: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class RemoteEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeDevice: ObserveDeviceUseCase,
    observeButtons: ObserveButtonsUseCase,
    private val saveButton: SaveButtonUseCase,
    private val deleteButton: DeleteButtonUseCase,
    private val saveDevice: SaveDeviceUseCase,
    private val buildIrCode: BuildIrCodeUseCase,
    private val createButton: CreateButtonUseCase
) : ViewModel() {

    private val deviceId: Long = savedStateHandle.get<Long>("deviceId") ?: 0L
    private val local = MutableStateFlow(EditorUiState())

    val uiState: StateFlow<EditorUiState> = combine(
        observeDevice(deviceId),
        observeButtons(deviceId),
        local
    ) { device, buttons, localState ->
        localState.copy(device = device, buttons = buttons)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EditorUiState()
    )

    fun startAdd() {
        local.update {
            it.copy(form = ButtonForm(frequencyKhz = it.device?.carrierFrequencyHz?.div(1000) ?: 38), formError = null)
        }
    }

    fun startEdit(button: RemoteButton) {
        val code = button.code
        local.update {
            it.copy(
                form = ButtonForm(
                    id = button.id,
                    label = button.label,
                    role = button.role,
                    protocol = code.protocol,
                    code = IrCodeCodec.toHex(code),
                    frequencyKhz = code.frequencyHz / 1000,
                    repeats = code.repeats,
                    toggle = (code as? IrCode.Rc5)?.toggle ?: false
                ),
                formError = null
            )
        }
    }

    fun updateForm(transform: (ButtonForm) -> ButtonForm) {
        local.update { state ->
            val form = state.form ?: return@update state
            state.copy(form = transform(form), formError = null)
        }
    }

    fun dismissForm() = local.update { it.copy(form = null, formError = null) }

    fun saveForm() {
        val state = local.value
        val form = state.form ?: return
        val device = state.device ?: return

        if (form.label.isBlank()) {
            local.update { it.copy(formError = "Escribe una etiqueta para el botón") }
            return
        }

        val parsed = buildIrCode(
            protocol = form.protocol,
            hexOrRaw = form.code,
            frequencyHz = form.frequencyKhz.coerceIn(30, 60) * 1000,
            repeats = form.repeats.coerceIn(1, 10),
            toggle = form.toggle
        )

        if (parsed == null) {
            local.update {
                it.copy(formError = instructionFor(form.protocol))
            }
            return
        }

        val existing = state.buttons.firstOrNull { it.id == form.id }
        val sortOrder = existing?.sortOrder
            ?: ((state.buttons.maxOfOrNull { it.sortOrder } ?: 0) + 1)

        viewModelScope.launch {
            saveButton(
                createButton(
                    deviceId = device.id,
                    label = form.label.trim(),
                    role = form.role,
                    section = sectionFor(form.role),
                    code = parsed,
                    sortOrder = sortOrder
                ).copy(id = form.id)
            )
            local.update {
                it.copy(form = null, message = if (form.isEditing) "Botón actualizado" else "Botón añadido")
            }
        }
    }

    fun delete(button: RemoteButton) {
        viewModelScope.launch { deleteButton(button.id) }
    }

    fun openRename() = local.update { it.copy(renameDialog = true) }

    fun closeRename() = local.update { it.copy(renameDialog = false) }

    fun renameDevice(name: String) {
        val device = local.value.device ?: return
        viewModelScope.launch {
            saveDevice(device.copy(name = name.trim().ifBlank { device.name }))
            local.update { it.copy(renameDialog = false, message = "Nombre actualizado") }
        }
    }

    fun clearMessage() = local.update { it.copy(message = null) }

    fun duplicate(button: RemoteButton) {
        viewModelScope.launch {
            saveButton(
                button.copy(
                    id = 0L,
                    label = "${button.label} (copia)",
                    sortOrder = (local.value.buttons.maxOfOrNull { it.sortOrder } ?: 0) + 1
                )
            )
        }
    }

    private fun sectionFor(role: ButtonRole): ButtonSection = when (role) {
        ButtonRole.POWER, ButtonRole.POWER_ON, ButtonRole.POWER_OFF -> ButtonSection.POWER
        ButtonRole.UP, ButtonRole.DOWN, ButtonRole.LEFT, ButtonRole.RIGHT, ButtonRole.OK,
        ButtonRole.BACK, ButtonRole.HOME, ButtonRole.MENU, ButtonRole.EXIT, ButtonRole.INFO,
        ButtonRole.INPUT -> ButtonSection.NAVIGATION
        ButtonRole.VOL_UP, ButtonRole.VOL_DOWN, ButtonRole.MUTE -> ButtonSection.VOLUME
        ButtonRole.CH_UP, ButtonRole.CH_DOWN -> ButtonSection.CHANNEL
        ButtonRole.DIGIT_0, ButtonRole.DIGIT_1, ButtonRole.DIGIT_2, ButtonRole.DIGIT_3,
        ButtonRole.DIGIT_4, ButtonRole.DIGIT_5, ButtonRole.DIGIT_6, ButtonRole.DIGIT_7,
        ButtonRole.DIGIT_8, ButtonRole.DIGIT_9 -> ButtonSection.DIGITS
        ButtonRole.PLAY, ButtonRole.PAUSE, ButtonRole.STOP, ButtonRole.NEXT, ButtonRole.PREVIOUS,
        ButtonRole.REWIND, ButtonRole.FORWARD, ButtonRole.RECORD -> ButtonSection.MEDIA
        ButtonRole.NETFLIX, ButtonRole.YOUTUBE -> ButtonSection.APPS
        ButtonRole.TEMP_UP, ButtonRole.TEMP_DOWN, ButtonRole.MODE, ButtonRole.FAN,
        ButtonRole.SWING, ButtonRole.TIMER, ButtonRole.TURBO, ButtonRole.SLEEP -> ButtonSection.CLIMATE
        else -> ButtonSection.CUSTOM
    }

    private fun instructionFor(protocol: IrProtocolId): String = when (protocol) {
        IrProtocolId.NEC -> "Los códigos NEC se escriben con 8 dígitos HEX (por ejemplo 04FB08F7)."
        IrProtocolId.NEC_EXTENDED -> "NEC extendido usa 8 dígitos HEX: dirección baja, dirección alta y comando (por ejemplo EAC717E8)."
        IrProtocolId.SAMSUNG -> "Samsung 32 usa 8 dígitos HEX (por ejemplo E0E040BF)."
        IrProtocolId.SONY_SIRC -> "Sony SIRC usa 4 dígitos HEX: dispositivo + comando (por ejemplo 0115)."
        IrProtocolId.RC5 -> "RC-5 usa 4 dígitos HEX: sistema + comando (por ejemplo 000C)."
        IrProtocolId.RAW -> "El modo RAW espera microsegundos separados por espacios (por ejemplo 9000 4500 560 560)."
    }
}
