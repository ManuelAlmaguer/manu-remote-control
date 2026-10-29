package com.manu.remotecontrol.presentation.settings

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manu.remotecontrol.domain.model.AppSettings
import com.manu.remotecontrol.domain.model.ThemeMode
import com.manu.remotecontrol.presentation.components.IrStatusCard
import com.manu.remotecontrol.presentation.components.InfoRow
import com.manu.remotecontrol.presentation.components.SectionHeader
import kotlin.math.roundToInt

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showLicenses by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader(title = "Apariencia")
            }
            item {
                SettingsCard {
                    Text("Tema", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            FilterChip(
                                selected = state.settings.themeMode == mode,
                                onClick = { viewModel.setTheme(mode) },
                                label = { Text(mode.displayName, style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    SwitchRow(
                        title = "Color dinámico (Material You)",
                        subtitle = "Toma la paleta del fondo de pantalla en Android 12 o superior",
                        checked = state.settings.dynamicColor,
                        onCheckedChange = viewModel::setDynamicColor
                    )
                }
            }

            item {
                SectionHeader(title = "Infrarrojos")
            }
            item {
                IrStatusCard(
                    hardware = state.hardware,
                    simulationForced = state.settings.forceSimulationMode
                )
            }
            item {
                SettingsCard {
                    InfoRow("Dispositivo", state.hardware.deviceLabel.ifBlank { "Desconocido" })
                    InfoRow("Sistema", state.hardware.androidVersion.ifBlank { "—" })
                    InfoRow(
                        "Feature CONSUMER_IR",
                        if (state.hardware.hasPackageFeature) "Disponible" else "No disponible"
                    )
                    InfoRow(
                        "hasIrEmitter()",
                        if (state.hardware.hasIrEmitter) "true" else "false"
                    )
                    InfoRow("Rangos de portadora", state.hardware.frequencyRangeLabel)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "La app comprueba PackageManager.FEATURE_CONSUMER_IR y " +
                            "ConsumerIrManager.hasIrEmitter() al arrancar y antes de cada emisión.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                CalibrationCard(
                    settings = state.settings,
                    hardwareRanges = state.hardware.frequencyRangeLabel,
                    onCarrierChange = viewModel::setCarrier
                )
            }
            item {
                SettingsCard {
                    SwitchRow(
                        title = "Modo Simulación",
                        subtitle = "No emite IR: muestra el patrón y el feedback visual (útil sin emisor)",
                        checked = state.settings.forceSimulationMode,
                        onCheckedChange = viewModel::setSimulation
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SwitchRow(
                        title = "Vibración háptica",
                        subtitle = "Vibración ligera al pulsar los botones",
                        checked = state.settings.hapticsEnabled,
                        onCheckedChange = viewModel::setHaptics
                    )
                }
            }

            item {
                SectionHeader(title = "Acerca de")
            }
            item {
                SettingsCard {
                    InfoRow("Versión", state.appVersion)
                    InfoRow("Base de datos IR", "Flipper-IRDB (CC0 1.0) + muestra propia")
                    InfoRow("Arquitectura", "MVVM + Clean Architecture")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showLicenses = true }) {
                        Icon(Icons.Rounded.Info, contentDescription = null, modifier = Modifier.height(18.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("  Licencias y créditos")
                    }
                    Text(
                        text = "Manu Remote Control funciona 100 % sin conexión: no hay cuentas, " +
                            "ni analítica, ni descargas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                SettingsCard {
                    Text("Cómo añadir códigos IR", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "1. Abre un control y entra en «Editar botones».\n" +
                            "2. Añade un botón, elige el protocolo (NEC, Samsung, Sony, RC-5 o RAW).\n" +
                            "3. Escribe el código HEX de 8 dígitos (NEC/Samsung), de 4 (Sony/RC-5) " +
                            "o el patrón de microsegundos (RAW).\n" +
                            "4. Ajusta la frecuencia: 38 kHz (NEC/Samsung), 40 kHz (Sony), 36 kHz (RC-5).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showLicenses) {
        AlertDialog(
            onDismissRequest = { showLicenses = false },
            title = { Text("Licencias y créditos") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LicenseBlock(
                        title = "Flipper-IRDB",
                        body = "Base de códigos IR utilizada como semilla del catálogo. " +
                            "Licencia CC0 1.0 (dominio público). https://github.com/Lucaslhm/Flipper-IRDB"
                    )
                    LicenseBlock(
                        title = "IRDB (probonopd)",
                        body = "Referencia de estructura de dispositivos y modelos. " +
                            "https://github.com/probonopd/irdb"
                    )
                    LicenseBlock(
                        title = "Inter",
                        body = "Tipografía Inter, SIL Open Font License 1.1."
                    )
                    LicenseBlock(
                        title = "Jetpack Compose, Room, Hilt, Navigation",
                        body = "Bibliotecas de Android con licencia Apache 2.0."
                    )
                    LicenseBlock(
                        title = "Material Symbols / Icons Extended",
                        body = "Iconografía de Google, Apache 2.0."
                    )
                    Text(
                        text = "Los códigos incluidos son marcas de sus respectivos fabricantes y se " +
                            "distribuyen con fines de interoperabilidad.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLicenses = false }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CalibrationCard(
    settings: AppSettings,
    hardwareRanges: String,
    onCarrierChange: (Int?) -> Unit
) {
    var manual by remember(settings.carrierFrequencyOverrideHz) {
        mutableStateOf(settings.carrierFrequencyOverrideHz != null)
    }
    SettingsCard {
        Text("Calibración de portadora", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Rangos del hardware: $hardwareRanges",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        SwitchRow(
            title = "Frecuencia manual",
            subtitle = "Desactívala para usar la frecuencia propia de cada protocolo",
            checked = manual,
            onCheckedChange = { enabled ->
                manual = enabled
                onCarrierChange(if (enabled) AppSettings.DEFAULT_CARRIER_HZ else null)
            }
        )
        if (manual) {
            val value = (settings.carrierFrequencyOverrideHz ?: AppSettings.DEFAULT_CARRIER_HZ).toFloat()
            Text(
                text = "${(value / 1000f).roundToInt()} kHz",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Slider(
                value = value,
                onValueChange = { onCarrierChange(it.roundToInt()) },
                valueRange = AppSettings.MIN_CARRIER_HZ.toFloat()..AppSettings.MAX_CARRIER_HZ.toFloat(),
                steps = 29
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(36_000, 38_000, 40_000, 56_000).forEach { preset ->
                    FilterChip(
                        selected = (settings.carrierFrequencyOverrideHz ?: 0) == preset,
                        onClick = { onCarrierChange(preset) },
                        label = { Text("${preset / 1000} kHz", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Si tu equipo responde de forma intermitente, prueba 38 kHz (NEC) o 40 kHz (Sony). " +
                "La calibración se aplica a todos los mandos.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LicenseBlock(title: String, body: String) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Default),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
