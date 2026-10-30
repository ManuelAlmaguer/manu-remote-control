package com.manu.remotecontrol.presentation.editor

import androidx.lifecycle.SavedStateHandle
import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.ButtonSection
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.domain.model.RemoteDevice
import com.manu.remotecontrol.domain.repository.DeviceRepository
import com.manu.remotecontrol.domain.usecase.BuildIrCodeUseCase
import com.manu.remotecontrol.domain.usecase.CreateButtonUseCase
import com.manu.remotecontrol.domain.usecase.DeleteButtonUseCase
import com.manu.remotecontrol.domain.usecase.ObserveButtonsUseCase
import com.manu.remotecontrol.domain.usecase.ObserveDeviceUseCase
import com.manu.remotecontrol.domain.usecase.SaveButtonUseCase
import com.manu.remotecontrol.domain.usecase.SaveDeviceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Regresión del bug «el botón Guardar no hace nada» en el editor de botones.
 *
 * El ViewModel leía `device` y `buttons` de un `MutableStateFlow` interno que nunca se
 * rellenaba (sólo el flujo combinado los tenía), así que `saveForm()` salía en el
 * `device ?: return` sin guardar, sin error y sin cerrar el diálogo. Ambos casos
 * (alta y edición) debían quedar cubiertos por tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RemoteEditorViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val deviceId = 42L

    private lateinit var repository: FakeDeviceRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeDeviceRepository(
            device = RemoteDevice(
                id = deviceId,
                name = "TV salón",
                brand = "Samsung",
                model = "UE40",
                type = com.manu.remotecontrol.domain.model.DeviceType.TV,
                carrierFrequencyHz = 38_000
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Crea el ViewModel dejando un colector activo sobre `uiState`, igual que hace la
     * pantalla con `collectAsStateWithLifecycle`. Sin colector, un `stateIn(WhileSubscribed)`
     * nunca emitiría y el test no reproduciría el comportamiento real.
     */
    private fun TestScope.startViewModel(): RemoteEditorViewModel {
        val viewModel = createViewModel()
        backgroundScope.launch { viewModel.uiState.collect { } }
        advanceUntilIdle()
        return viewModel
    }

    private fun createViewModel() = RemoteEditorViewModel(
        savedStateHandle = SavedStateHandle(mapOf("deviceId" to deviceId)),
        observeDevice = ObserveDeviceUseCase(repository),
        observeButtons = ObserveButtonsUseCase(repository),
        saveButton = SaveButtonUseCase(repository),
        deleteButton = DeleteButtonUseCase(repository),
        saveDevice = SaveDeviceUseCase(repository),
        buildIrCode = BuildIrCodeUseCase(),
        createButton = CreateButtonUseCase()
    )

    @Test
    fun `guardar un boton nuevo lo persiste y cierra el dialogo`() = runTest(dispatcher) {
        val viewModel = startViewModel()

        viewModel.startAdd()
        advanceUntilIdle()
        val form = viewModel.uiState.value.form
        assertNotNull("El formulario de alta debe abrirse", form)

        viewModel.updateForm { it.copy(label = "Volumen +", code = "04FB08F7") }
        advanceUntilIdle()
        viewModel.saveForm()
        advanceUntilIdle()

        assertEquals("El botón debe haberse guardado una vez", 1, repository.savedButtons.size)
        val saved = repository.savedButtons.single()
        assertEquals("Volumen +", saved.label)
        assertEquals(deviceId, saved.deviceId)
        assertTrue("Un botón nuevo debe insertarse con id 0", saved.id == 0L)
        assertNull("El diálogo debe cerrarse al guardar", viewModel.uiState.value.form)
        assertEquals("Botón añadido", viewModel.uiState.value.message)
        assertNull(viewModel.uiState.value.formError)
    }

    @Test
    fun `guardar un boton editado actualiza el existente sin duplicarlo`() = runTest(dispatcher) {
        val existing = RemoteButton(
            id = 7L,
            deviceId = deviceId,
            label = "Subir volumen",
            role = ButtonRole.VOL_UP,
            section = ButtonSection.VOLUME,
            code = IrCode.Nec(address = 0x04, command = 0x08),
            sortOrder = 3
        )
        repository.buttons.value = listOf(existing)

        val viewModel = startViewModel()

        viewModel.startEdit(existing)
        advanceUntilIdle()
        assertTrue("Debe abrirse en modo edición", viewModel.uiState.value.form?.isEditing == true)

        viewModel.updateForm { it.copy(label = "Más volumen") }
        advanceUntilIdle()
        viewModel.saveForm()
        advanceUntilIdle()

        assertEquals("No debe crearse un botón nuevo", 1, repository.savedButtons.size)
        val saved = repository.savedButtons.single()
        assertEquals("Debe conservar el id del botón editado", 7L, saved.id)
        assertEquals("Más volumen", saved.label)
        assertEquals("Debe conservar el orden original", 3, saved.sortOrder)
        assertNull(viewModel.uiState.value.form)
        assertEquals("Botón actualizado", viewModel.uiState.value.message)
    }

    @Test
    fun `guardar sin etiqueta muestra error y no guarda`() = runTest(dispatcher) {
        val viewModel = startViewModel()

        viewModel.startAdd()
        advanceUntilIdle()
        viewModel.updateForm { it.copy(label = "   ", code = "04FB08F7") }
        advanceUntilIdle()
        viewModel.saveForm()
        advanceUntilIdle()

        assertTrue(repository.savedButtons.isEmpty())
        assertNotNull("El error debe ser visible en el diálogo", viewModel.uiState.value.formError)
        assertNotNull("El formulario debe seguir abierto", viewModel.uiState.value.form)
    }

    @Test
    fun `guardar con codigo invalido explica el formato esperado`() = runTest(dispatcher) {
        val viewModel = startViewModel()

        viewModel.startAdd()
        advanceUntilIdle()
        viewModel.updateForm {
            it.copy(label = "Encender", protocol = IrProtocolId.NEC, code = "XYZ")
        }
        advanceUntilIdle()
        viewModel.saveForm()
        advanceUntilIdle()

        assertTrue(repository.savedButtons.isEmpty())
        assertTrue(
            viewModel.uiState.value.formError.orEmpty().contains("8 dígitos HEX")
        )
    }

    /** Fake en memoria del repositorio: registra lo que el editor intenta guardar. */
    private class FakeDeviceRepository(device: RemoteDevice?) : DeviceRepository {

        val deviceFlow = MutableStateFlow(device)
        val buttons = MutableStateFlow<List<RemoteButton>>(emptyList())
        val savedButtons = mutableListOf<RemoteButton>()

        override fun observeDevices(): Flow<List<RemoteDevice>> =
            deviceFlow.map { listOfNotNull(it) }

        override fun observeDevice(deviceId: Long): Flow<RemoteDevice?> = deviceFlow

        override fun observeButtons(deviceId: Long): Flow<List<RemoteButton>> = buttons

        override suspend fun getDevice(deviceId: Long): RemoteDevice? = deviceFlow.value

        override suspend fun getButtons(deviceId: Long): List<RemoteButton> = buttons.value

        override suspend fun saveDevice(device: RemoteDevice): Long = device.id

        override suspend fun deleteDevice(deviceId: Long) = Unit

        override suspend fun setFavorite(deviceId: Long, favorite: Boolean) = Unit

        override suspend fun saveButton(button: RemoteButton): Long {
            savedButtons += button
            return button.id
        }

        override suspend fun saveButtons(buttons: List<RemoteButton>) {
            savedButtons += buttons
        }

        override suspend fun deleteButton(buttonId: Long) {
            buttons.value = buttons.value.filterNot { it.id == buttonId }
        }

        override suspend fun createFromCatalog(catalogKey: String, customName: String?): Long? = null
    }
}
