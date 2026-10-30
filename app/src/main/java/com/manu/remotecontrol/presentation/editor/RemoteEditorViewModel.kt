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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    /** `true` en cuanto el dispositivo emite por primera vez (aunque emita `null`). */
    val deviceLoaded: Boolean = false,
    val buttons: List<RemoteButton> = emptyList(),
    val form: ButtonForm? = null,
    val formError: String? = null,
    val isSaving: Boolean = false,
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

    /**
     * Estado único de la pantalla.
     *
     * Antes había dos fuentes (`local` + el `combine` de Room) y sólo el flujo combinado
     * llevaba `device` y `buttons`; `local` se quedaba siempre vacío. Por eso `saveForm()`
     * salía en el `device ?: return` y el botón Guardar no hacía absolutamente nada.
     */
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeDevice(deviceId).collect { device ->
                _uiState.update { it.copy(device = device, deviceLoaded = true) }
            }
        }
        viewModelScope.launch {
            observeButtons(deviceId).collect { buttons ->
                _uiState.update { it.copy(buttons = buttons) }
            }
        }
    }

    fun startAdd() {
        _uiState.update {
            it.copy(
                form = ButtonForm(frequencyKhz = it.device?.carrierFrequencyHz?.div(1000) ?: 38),
                formError = null
            )
        }
    }

    fun startEdit(button: RemoteButton) {
        val code = button.code
        _uiState.update {
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
        _uiState.update { state ->
            val form = state.form ?: return@update state
            state.copy(form = transform(form), formError = null)
        }
    }

    fun dismissForm() = _uiState.update { it.copy(form = null, formError = null, isSaving = false) }

    /**
     * Valida el formulario y guarda el botón (alta si `form.id == 0`, edición si ya existe).
     * Cualquier fallo se muestra dentro del diálogo: nunca se sale en silencio.
     */
    fun saveForm() {
        val state = _uiState.value
        val form = state.form ?: return
        if (state.isSaving) return

        if (form.label.isBlank()) {
            _uiState.update { it.copy(formError = "Escribe una etiqueta para el botón") }
            return
        }

        val device = state.device
        if (device == null) {
            _uiState.update {
                it.copy(
                    formError = if (state.deviceLoaded) {
                        "Este control ya no existe. Vuelve atrás y ábrelo de nuevo."
                    } else {
                        "El control aún se está cargando. Espera un segundo e inténtalo otra vez."
                    }
                )
            }
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
            _uiState.update {
                it.copy(formError = instructionFor(form.protocol))
            }
            return
        }

        val existing = if (form.isEditing) state.buttons.firstOrNull { it.id == form.id } else null
        val sortOrder = existing?.sortOrder
            ?: ((state.buttons.maxOfOrNull { it.sortOrder } ?: 0) + 1)

        _uiState.update { it.copy(isSaving = true, formError = null) }

        viewModelScope.launch {
            runCatching {
                saveButton(
                    createButton(
                        deviceId = device.id,
                        label = form.label.trim(),
                        role = form.role,
                        section = sectionFor(form.role),
                        code = parsed,
                        sortOrder = sortOrder
                    ).copy(id = if (existing != null) existing.id else 0L)
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        form = null,
                        isSaving = false,
                        message = if (form.isEditing) "Botón actualizado" else "Botón añadido"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        formError = "No se pudo guardar el botón: ${error.message ?: "error desconocido"}"
                    )
                }
            }
        }
    }

    fun delete(button: RemoteButton) {
        viewModelScope.launch { deleteButton(button.id) }
    }

    fun openRename() = _uiState.update { it.copy(renameDialog = true) }

    fun closeRename() = _uiState.update { it.copy(renameDialog = false) }

    fun renameDevice(name: String) {
        val device = _uiState.value.device ?: return
        viewModelScope.launch {
            saveDevice(device.copy(name = name.trim().ifBlank { device.name }))
            _uiState.update { it.copy(renameDialog = false, message = "Nombre actualizado") }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(message = null) }

    fun duplicate(button: RemoteButton) {
        viewModelScope.launch {
            saveButton(
                button.copy(
                    id = 0L,
                    label = "${button.label} (copia)",
                    sortOrder = (_uiState.value.buttons.maxOfOrNull { it.sortOrder } ?: 0) + 1
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
