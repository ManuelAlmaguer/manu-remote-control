package com.manu.remotecontrol.presentation.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manu.remotecontrol.domain.ir.IrCodeCodec
import com.manu.remotecontrol.domain.ir.IrProtocolId
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.RemoteButton

/**
 * Editor de botones: permite crear un control desde cero o retocar cualquier mando
 * guardado, asignando etiqueta, rol, protocolo, código HEX/RAW, frecuencia y repeticiones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteEditorScreen(
    onBack: () -> Unit,
    viewModel: RemoteEditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = {
                    Column {
                        Text(
                            text = state.device?.name ?: "Editor de botones",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${state.buttons.size} botones",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::openRename) {
                        Icon(Icons.Rounded.DriveFileRenameOutline, contentDescription = "Renombrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::startAdd,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Añadir botón") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cada botón guarda su código (HEX o RAW) y su protocolo. " +
                            "Puedes copiar los códigos de la base incluida o de cualquier mando documentado.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
            if (state.buttons.isEmpty()) {
                item {
                    Text(
                        text = "Todavía no hay botones. Añade el primero con el botón de abajo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(state.buttons, key = { it.id }) { button ->
                ButtonRow(
                    button = button,
                    onEdit = { viewModel.startEdit(button) },
                    onDuplicate = { viewModel.duplicate(button) },
                    onDelete = { viewModel.delete(button) }
                )
            }
        }
    }

    val form = state.form
    if (form != null) {
        ButtonFormDialog(
            form = form,
            protocolOptions = state.protocolOptions,
            error = state.formError,
            onDismiss = viewModel::dismissForm,
            onUpdate = viewModel::updateForm,
            onSave = viewModel::saveForm
        )
    }

    if (state.renameDialog) {
        var name by remember(state.device?.id) { mutableStateOf(state.device?.name.orEmpty()) }
        AlertDialog(
            onDismissRequest = viewModel::closeRename,
            title = { Text("Renombrar control") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.renameDevice(name) }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = viewModel::closeRename) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ButtonRow(
    button: RemoteButton,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = button.label, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "${button.role.defaultLabel} · ${button.section.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${button.code.protocol.displayName} · ${IrCodeCodec.toHex(button.code).take(26)}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Rounded.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onDuplicate) {
                Icon(Icons.Rounded.ContentCopy, contentDescription = "Duplicar")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
private fun ButtonFormDialog(
    form: ButtonForm,
    protocolOptions: List<IrProtocolId>,
    error: String?,
    onDismiss: () -> Unit,
    onUpdate: ((ButtonForm) -> ButtonForm) -> Unit,
    onSave: () -> Unit
) {
    var roleMenu by remember { mutableStateOf(false) }
    var protocolMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (form.isEditing) "Editar botón" else "Nuevo botón") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = form.label,
                    onValueChange = { value -> onUpdate { it.copy(label = value) } },
                    label = { Text("Etiqueta") },
                    placeholder = { Text("Volumen +") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Rol", style = MaterialTheme.typography.labelMedium)
                Box {
                    OutlinedButton(
                        onClick = { roleMenu = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(form.role.defaultLabel, modifier = Modifier.weight(1f))
                        Text("Cambiar", style = MaterialTheme.typography.labelSmall)
                    }
                    DropdownMenu(
                        expanded = roleMenu,
                        onDismissRequest = { roleMenu = false },
                        modifier = Modifier.heightIn(max = 360.dp)
                    ) {
                        ButtonRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.defaultLabel) },
                                onClick = {
                                    roleMenu = false
                                    onUpdate { it.copy(role = role) }
                                }
                            )
                        }
                    }
                }

                Text("Protocolo", style = MaterialTheme.typography.labelMedium)
                Box {
                    OutlinedButton(
                        onClick = { protocolMenu = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(form.protocol.displayName, modifier = Modifier.weight(1f))
                        Text("Cambiar", style = MaterialTheme.typography.labelSmall)
                    }
                    DropdownMenu(
                        expanded = protocolMenu,
                        onDismissRequest = { protocolMenu = false }
                    ) {
                        protocolOptions.forEach { protocol ->
                            DropdownMenuItem(
                                text = { Text(protocol.displayName) },
                                onClick = {
                                    protocolMenu = false
                                    onUpdate { it.copy(protocol = protocol) }
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = form.code,
                    onValueChange = { value -> onUpdate { it.copy(code = value) } },
                    label = {
                        Text(if (form.protocol == IrProtocolId.RAW) "Patrón RAW (µs)" else "Código HEX")
                    },
                    placeholder = {
                        Text(if (form.protocol == IrProtocolId.RAW) "9000 4500 560 560 …" else "04FB08F7")
                    },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    singleLine = form.protocol != IrProtocolId.RAW,
                    minLines = if (form.protocol == IrProtocolId.RAW) 3 else 1,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = form.frequencyKhz.toString(),
                        onValueChange = { value ->
                            val khz = value.filter { it.isDigit() }.take(2).toIntOrNull()
                            onUpdate { it.copy(frequencyKhz = khz ?: 0) }
                        },
                        label = { Text("kHz") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = form.repeats.toString(),
                        onValueChange = { value ->
                            val repeats = value.filter { it.isDigit() }.take(2).toIntOrNull()
                            onUpdate { it.copy(repeats = repeats ?: 1) }
                        },
                        label = { Text("Repeticiones") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (form.protocol == IrProtocolId.RC5) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Bit de alternancia (toggle)", modifier = Modifier.weight(1f))
                        Switch(
                            checked = form.toggle,
                            onCheckedChange = { checked -> onUpdate { it.copy(toggle = checked) } }
                        )
                    }
                }

                Text(
                    text = helperFor(form.protocol),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (error != null) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = { Button(onClick = onSave) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

private fun helperFor(protocol: IrProtocolId): String = when (protocol) {
    IrProtocolId.NEC -> "NEC: 8 dígitos HEX. Ejemplo 04FB08F7 = dirección 0x04, comando 0x08."
    IrProtocolId.NEC_EXTENDED -> "NEC extendido: 8 dígitos HEX (dirección baja, alta, comando y su inverso). Ejemplo EAC717E8."
    IrProtocolId.SAMSUNG -> "Samsung 32: 8 dígitos HEX. Ejemplo E0E040BF."
    IrProtocolId.SONY_SIRC -> "Sony SIRC: 4 dígitos HEX (dispositivo + comando). Ejemplo 0115."
    IrProtocolId.RC5 -> "RC-5: 4 dígitos HEX (sistema 0-31 + comando 0-63). Ejemplo 000C."
    IrProtocolId.RAW -> "RAW: duraciones en microsegundos separadas por espacios, empezando por marca."
}
