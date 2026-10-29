package com.manu.remotecontrol.presentation.adddevice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.CatalogDevice
import com.manu.remotecontrol.domain.model.DeviceType
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.presentation.components.DeviceTypeBadge
import com.manu.remotecontrol.presentation.components.IrSignalIndicator
import com.manu.remotecontrol.presentation.components.SectionHeader
import com.manu.remotecontrol.presentation.components.deviceTypeIcon
import com.manu.remotecontrol.presentation.theme.LocalManuExtraColors

/**
 * Asistente paso a paso: Tipo → Marca → Modelo → Prueba de código.
 * También ofrece crear un control desde cero (va directo al editor de botones).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddDeviceScreen(
    onBack: () -> Unit,
    onDeviceCreated: (Long) -> Unit,
    onCustomCreated: (Long) -> Unit,
    viewModel: AddDeviceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCustomDialog by remember { mutableStateOf(false) }
    var customType by remember { mutableStateOf(DeviceType.TV) }

    LaunchedEffect(state.createdDeviceId) {
        val id = state.createdDeviceId ?: return@LaunchedEffect
        if (state.isCustomCreation) onCustomCreated(id) else onDeviceCreated(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir dispositivo") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!viewModel.goBack()) onBack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Stepper(current = state.step)
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Box(modifier = Modifier.weight(1f)) {
                when (state.step) {
                    AddDeviceStep.TYPE -> TypeStep(
                        onSelect = viewModel::selectType,
                        onCreateCustom = {
                            customType = state.selectedType ?: DeviceType.TV
                            showCustomDialog = true
                        }
                    )

                    AddDeviceStep.BRAND -> BrandStep(
                        state = state,
                        onQueryChange = viewModel::updateQuery,
                        onBrandSelected = viewModel::selectBrand,
                        onDeviceSelected = viewModel::selectDevice,
                        onCreateCustom = { showCustomDialog = true }
                    )

                    AddDeviceStep.MODEL -> ModelStep(
                        state = state,
                        onDeviceSelected = viewModel::selectDevice
                    )

                    AddDeviceStep.TEST -> TestStep(
                        state = state,
                        onTest = viewModel::testButton,
                        onSave = viewModel::saveSelected
                    )
                }
            }
            if (state.step == AddDeviceStep.TEST) {
                Button(
                    onClick = viewModel::saveSelected,
                    enabled = !state.isSaving && state.selectedCatalog != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(if (state.isSaving) "Guardando…" else "Guardar dispositivo")
                }
            }
        }
    }

    if (showCustomDialog) {
        CustomDeviceDialog(
            initialType = customType,
            onDismiss = { showCustomDialog = false },
            onConfirm = { name, brand, model, type ->
                showCustomDialog = false
                viewModel.createCustomDevice(name, brand, model, type)
            }
        )
    }

    state.error?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text("No se pudo completar") },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = viewModel::clearError) { Text("Entendido") } }
        )
    }
}

@Composable
private fun Stepper(current: AddDeviceStep) {
    val steps = listOf(
        AddDeviceStep.TYPE to "Tipo",
        AddDeviceStep.BRAND to "Marca",
        AddDeviceStep.MODEL to "Modelo",
        AddDeviceStep.TEST to "Prueba"
    )
    val currentIndex = steps.indexOfFirst { it.first == current }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, (step, label) ->
            val active = index <= currentIndex
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            if (active) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (active) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (index == currentIndex) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                        .height(2.dp)
                        .background(
                            if (index < currentIndex) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}

@Composable
private fun TypeStep(
    onSelect: (DeviceType) -> Unit,
    onCreateCustom: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(DeviceType.entries) { type ->
            Card(
                onClick = { onSelect(type) },
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = deviceTypeIcon(type),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
        item {
            OutlinedButton(
                onClick = onCreateCustom,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Text("Control desde cero", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun BrandStep(
    state: AddDeviceUiState,
    onQueryChange: (String) -> Unit,
    onBrandSelected: (String) -> Unit,
    onDeviceSelected: (CatalogDevice) -> Unit,
    onCreateCustom: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Buscar marca o modelo…") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (state.query.isBlank()) {
                items(state.brands) { brand ->
                    Card(
                        onClick = { onBrandSelected(brand) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = brand,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Ver modelos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = onCreateCustom,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("No encuentro mi marca · control desde cero")
                    }
                }
            } else {
                items(state.devices, key = { it.key }) { device ->
                    CatalogDeviceRow(device = device, onClick = { onDeviceSelected(device) })
                }
                if (state.devices.isEmpty()) {
                    item { Text("Sin resultados. Prueba con otra marca o crea un control desde cero.") }
                }
            }
        }
    }
}

@Composable
private fun ModelStep(
    state: AddDeviceUiState,
    onDeviceSelected: (CatalogDevice) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SectionHeader(
            title = state.selectedBrand ?: "Modelos",
            subtitle = "Elige el modelo o el mando compatible",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.devices, key = { it.key }) { device ->
                CatalogDeviceRow(device = device, onClick = { onDeviceSelected(device) })
            }
            if (state.devices.isEmpty()) {
                item { Text("No hay modelos para esta marca. Crea un control desde cero.") }
            }
        }
    }
}

@Composable
private fun CatalogDeviceRow(device: CatalogDevice, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DeviceTypeBadge(type = device.type, size = 44)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = device.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${device.brand} · ${device.model}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${device.buttonCount} botones · ${device.frequencyHz / 1000} kHz",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TestStep(
    state: AddDeviceUiState,
    onTest: (RemoteButton) -> Unit,
    onSave: () -> Unit
) {
    val extras = LocalManuExtraColors.current
    val catalog = state.selectedCatalog
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = catalog?.name ?: "Control",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = catalog?.description.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pulsa un botón para probarlo contra tu equipo. Si no hay emisor IR, la app mostrará el modo simulación.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IrSignalIndicator(
                    pulse = if (state.lastTest != null) 1L else 0L,
                    mode = state.lastTest?.mode
                )
                Text(
                    text = state.lastTest?.message ?: "Sin pruebas todavía",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (state.lastTest?.mode == com.manu.remotecontrol.domain.model.TransmissionMode.FAILED)
                        MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(state.previewButtons) { button ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = button.label,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = button.code.protocol.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = { onTest(button) }) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = "Probar",
                            tint = extras.irLed
                        )
                    }
                }
            }
        }
        item {
            Text(
                text = "¿No funciona ningún botón? Prueba otro modelo compatible o crea un control desde cero con los códigos HEX de tu mando.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CustomDeviceDialog(
    initialType: DeviceType,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, DeviceType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(initialType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Control desde cero") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    placeholder = { Text("Mi TV del salón") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Marca (opcional)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Modelo (opcional)") },
                    singleLine = true
                )
                Text("Tipo", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DeviceType.entries.forEach { option ->
                        FilterChip(
                            selected = type == option,
                            onClick = { type = option },
                            label = { Text(option.displayName, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Text(
                    text = "Se creará un control vacío y podrás añadir botones con su código HEX y protocolo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, brand, model, type) }) {
                Text("Crear control")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
