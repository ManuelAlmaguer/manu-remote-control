package com.manu.remotecontrol.presentation.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manu.remotecontrol.domain.model.DeviceType
import com.manu.remotecontrol.domain.model.TransmissionMode
import androidx.compose.material3.Button
import com.manu.remotecontrol.presentation.components.IrSignalIndicator
import com.manu.remotecontrol.presentation.theme.LocalManuExtraColors

/**
 * Pantalla principal del control remoto.
 *
 * La composición se adapta a la orientación: en vertical se apila el panel completo y en
 * horizontal (o en tablets) se reparte en dos columnas para que todo quede a la vista.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteControlScreen(
    onBack: () -> Unit,
    onEditButtons: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: RemoteViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var menuOpen by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onBack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                            text = state.device?.name ?: "Control",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = state.device?.displaySubtitle?.ifBlank {
                                state.device?.type?.displayName.orEmpty()
                            }.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IrSignalIndicator(pulse = state.pulse, mode = state.lastResult?.mode)
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "Más opciones")
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(
                            text = { Text("Editar botones") },
                            leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
                            onClick = {
                                menuOpen = false
                                state.device?.let { onEditButtons(it.id) }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Ajustes") },
                            leadingIcon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                            onClick = {
                                menuOpen = false
                                onOpenSettings()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar control") },
                            leadingIcon = { Icon(Icons.Rounded.DeleteOutline, contentDescription = null) },
                            onClick = {
                                menuOpen = false
                                confirmDelete = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isWide = maxWidth > 620.dp && maxWidth > maxHeight
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StatusStrip(state = state, onDismiss = viewModel::clearMessage)

                val device = state.device
                if (device == null) {
                    if (!state.isLoading) {
                        EmptyRemoteCard(
                            title = "Control no encontrado",
                            message = "Este dispositivo ya no existe.",
                            actionLabel = "Volver",
                            onAction = onBack
                        )
                    }
                } else if (state.buttons.isEmpty()) {
                    EmptyRemoteCard(
                        title = "Sin botones todavía",
                        message = "Añade botones con su código HEX, protocolo y frecuencia para empezar a usar este control.",
                        actionLabel = "Añadir botones",
                        onAction = { onEditButtons(device.id) }
                    )
                } else {
                    when (device.type) {
                        DeviceType.AC -> AcRemoteContent(
                            state = state,
                            onPress = viewModel::onButtonPress,
                            onTemperatureChange = viewModel::onTemperatureChange,
                            hapticsEnabled = state.settings.hapticsEnabled,
                            isWide = isWide,
                            modifier = Modifier.fillMaxWidth()
                        )

                        else -> TvRemoteContent(
                            state = state,
                            onPress = viewModel::onButtonPress,
                            hapticsEnabled = state.settings.hapticsEnabled,
                            isWide = isWide,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Los mandos reales envían el estado completo del equipo en cada pulsación: " +
                            "si un botón no responde, prueba el modelo equivalente de la lista o edita su código.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Eliminar control") },
            text = { Text("Se eliminará el dispositivo y todos sus botones.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    viewModel.onDeleteDevice()
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") }
            }
        )
    }
}

/** Franja de estado con el último envío (real o simulado). */
@Composable
private fun StatusStrip(state: RemoteUiState, onDismiss: () -> Unit) {
    val extras = LocalManuExtraColors.current
    val result = state.lastResult
    val message = when {
        result != null -> result.message
        state.simulationActive -> "Modo Simulación: las pulsaciones se registran pero no se emiten."
        else -> "Emisor IR listo · ${state.hardware.frequencyRangeLabel}"
    }
    val color = when {
        result?.mode == TransmissionMode.FAILED -> MaterialTheme.colorScheme.errorContainer
        result?.mode == TransmissionMode.SIMULATED || state.simulationActive -> extras.simulated.copy(alpha = 0.14f)
        else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IrSignalIndicator(
                pulse = state.pulse,
                mode = result?.mode,
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(1f)
            )
            if (result != null) {
                TextButton(onClick = onDismiss) {
                    Text("OK", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun EmptyRemoteCard(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}
