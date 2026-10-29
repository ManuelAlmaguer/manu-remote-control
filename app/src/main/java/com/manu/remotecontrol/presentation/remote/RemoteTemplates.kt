package com.manu.remotecontrol.presentation.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.presentation.components.DPad
import com.manu.remotecontrol.presentation.components.KeyStyle
import com.manu.remotecontrol.presentation.components.NumericKeypad
import com.manu.remotecontrol.presentation.components.PowerKey
import com.manu.remotecontrol.presentation.components.RemoteKey
import com.manu.remotecontrol.presentation.components.RockerKey
import com.manu.remotecontrol.presentation.components.SectionHeader
import com.manu.remotecontrol.presentation.components.roleIcon
import com.manu.remotecontrol.presentation.theme.LocalManuExtraColors

/** Panel de un televisor, reproductor o equipo de audio. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TvRemoteContent(
    state: RemoteUiState,
    onPress: (RemoteButton) -> Unit,
    hapticsEnabled: Boolean,
    isWide: Boolean,
    modifier: Modifier = Modifier
) {
    val power = state.button(ButtonRole.POWER)
        ?: state.buttons.firstOrNull {
            it.role == ButtonRole.POWER_ON || it.role == ButtonRole.POWER_OFF
        }
    val up = state.button(ButtonRole.UP)
    val down = buttonOrNull(state, ButtonRole.DOWN)
    val left = buttonOrNull(state, ButtonRole.LEFT)
    val right = buttonOrNull(state, ButtonRole.RIGHT)
    val ok = buttonOrNull(state, ButtonRole.OK)
    val volUp = buttonOrNull(state, ButtonRole.VOL_UP)
    val volDown = buttonOrNull(state, ButtonRole.VOL_DOWN)
    val chUp = buttonOrNull(state, ButtonRole.CH_UP)
    val chDown = buttonOrNull(state, ButtonRole.CH_DOWN)
    val mute = buttonOrNull(state, ButtonRole.MUTE)
    val input = buttonOrNull(state, ButtonRole.INPUT)
    val digits = (0..9).mapNotNull { state.button(ButtonRole.digit(it)) }
    val media = listOfNotNull(
        buttonOrNull(state, ButtonRole.REWIND),
        buttonOrNull(state, ButtonRole.PREVIOUS),
        buttonOrNull(state, ButtonRole.PLAY) ?: buttonOrNull(state, ButtonRole.PAUSE),
        buttonOrNull(state, ButtonRole.NEXT),
        buttonOrNull(state, ButtonRole.FORWARD),
        buttonOrNull(state, ButtonRole.STOP)
    )

    val powerCluster: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (input != null) {
                RemoteKey(
                    label = input.label,
                    icon = roleIcon(input.role),
                    onClick = { onPress(input) },
                    hapticsEnabled = hapticsEnabled,
                    size = 52.dp,
                    style = KeyStyle.Outlined
                )
            } else {
                Spacer(modifier = Modifier.size(52.dp))
            }
            if (power != null) {
                PowerKey(
                    onClick = { onPress(power) },
                    hapticsEnabled = hapticsEnabled,
                    size = 74.dp
                )
            }
            if (mute != null) {
                RemoteKey(
                    label = mute.label,
                    icon = roleIcon(mute.role),
                    onClick = { onPress(mute) },
                    hapticsEnabled = hapticsEnabled,
                    size = 52.dp,
                    style = KeyStyle.Outlined
                )
            } else {
                Spacer(modifier = Modifier.size(52.dp))
            }
        }
    }

    val navigationCluster: @Composable () -> Unit = {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (up != null && down != null && left != null && right != null && ok != null) {
                DPad(
                    onUp = { onPress(up) },
                    onDown = { onPress(down) },
                    onLeft = { onPress(left) },
                    onRight = { onPress(right) },
                    onOk = { onPress(ok) },
                    hapticsEnabled = hapticsEnabled
                )
            }
        }
    }

    val rockerCluster: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (volUp != null && volDown != null) {
                StringRocker(
                    topLabel = "+",
                    bottomLabel = "−",
                    caption = "Vol",
                    onTop = { onPress(volUp) },
                    onBottom = { onPress(volDown) },
                    hapticsEnabled = hapticsEnabled
                )
            }
            if (chUp != null && chDown != null) {
                StringRocker(
                    topLabel = "▲",
                    bottomLabel = "▼",
                    caption = "Canal",
                    onTop = { onPress(chUp) },
                    onBottom = { onPress(chDown) },
                    hapticsEnabled = hapticsEnabled
                )
            }
        }
    }

    val digitsCluster: @Composable () -> Unit = {
        if (digits.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NumericKeypad(
                    onDigit = { digit ->
                        state.button(ButtonRole.digit(digit))?.let(onPress)
                    },
                    hapticsEnabled = hapticsEnabled
                )
            }
        }
    }

    val mediaCluster: @Composable () -> Unit = {
        if (media.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                media.forEach { button ->
                    RemoteKey(
                        label = button.label,
                        icon = roleIcon(button.role),
                        showLabel = false,
                        onClick = { onPress(button) },
                        hapticsEnabled = hapticsEnabled,
                        size = 46.dp,
                        style = KeyStyle.Outlined
                    )
                }
            }
        }
    }

    val extrasCluster: @Composable () -> Unit = {
        if (state.extraButtons.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = "Otros botones")
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.extraButtons.forEach { button ->
                        RemoteKey(
                            label = button.label,
                            icon = roleIcon(button.role),
                            onClick = { onPress(button) },
                            hapticsEnabled = hapticsEnabled,
                            size = 58.dp,
                            shape = MaterialTheme.shapes.medium,
                            style = KeyStyle.Tonal,
                            showLabel = true
                        )
                    }
                }
            }
        }
    }

    if (isWide) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                powerCluster()
                navigationCluster()
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                rockerCluster()
                digitsCluster()
                mediaCluster()
                extrasCluster()
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            powerCluster()
            navigationCluster()
            rockerCluster()
            digitsCluster()
            mediaCluster()
            extrasCluster()
        }
    }
}

@Composable
private fun StringRocker(
    topLabel: String,
    bottomLabel: String,
    caption: String,
    onTop: () -> Unit,
    onBottom: () -> Unit,
    hapticsEnabled: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        RockerKey(
            topLabel = topLabel,
            bottomLabel = bottomLabel,
            onTop = onTop,
            onBottom = onBottom,
            hapticsEnabled = hapticsEnabled
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Panel de aire acondicionado: temperatura, modo, velocidad del ventilador, oscilación y
 * encendido/apagado. Cada botón envía la trama original del mando del equipo.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AcRemoteContent(
    state: RemoteUiState,
    onPress: (RemoteButton) -> Unit,
    onTemperatureChange: (Int) -> Unit,
    hapticsEnabled: Boolean,
    isWide: Boolean,
    modifier: Modifier = Modifier
) {
    val extras = LocalManuExtraColors.current
    val modes = state.buttonsWithRole(ButtonRole.MODE)
    val fans = state.buttonsWithRole(ButtonRole.FAN)
    val swings = state.buttonsWithRole(ButtonRole.SWING)
    val power = state.button(ButtonRole.POWER)
    val powerOn = state.button(ButtonRole.POWER_ON)
    val powerOff = state.button(ButtonRole.POWER_OFF)
    val tempUp = state.button(ButtonRole.TEMP_UP)
    val tempDown = state.button(ButtonRole.TEMP_DOWN)
    var selectedMode by remember { mutableStateOf<String?>(null) }
    var selectedFan by remember { mutableStateOf<String?>(null) }

    val temperatureCard: @Composable () -> Unit = {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TEMPERATURA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${state.temperature}°",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 78.sp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Celsius",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        RemoteKey(
                            label = "Subir temperatura",
                            icon = Icons.Rounded.Add,
                            showLabel = false,
                            onClick = { onTemperatureChange(+1) },
                            hapticsEnabled = hapticsEnabled,
                            size = 54.dp,
                            style = KeyStyle.Filled
                        )
                        RemoteKey(
                            label = "Bajar temperatura",
                            icon = Icons.Rounded.Remove,
                            showLabel = false,
                            onClick = { onTemperatureChange(-1) },
                            hapticsEnabled = hapticsEnabled,
                            size = 54.dp,
                            style = KeyStyle.Outlined
                        )
                    }
                }
                if (tempUp == null && tempDown == null) {
                    Text(
                        text = "Este mando no incluye botones de temperatura",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }

    val powerCluster: @Composable () -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (powerOn != null) {
                RemoteKey(
                    label = "Encender",
                    icon = Icons.Rounded.AcUnit,
                    onClick = { onPress(powerOn) },
                    hapticsEnabled = hapticsEnabled,
                    size = 64.dp,
                    style = KeyStyle.Filled
                )
            }
            if (power != null) {
                PowerKey(
                    onClick = { onPress(power) },
                    hapticsEnabled = hapticsEnabled,
                    size = 68.dp
                )
            }
            if (powerOff != null) {
                RemoteKey(
                    label = "Apagar",
                    icon = Icons.Rounded.AcUnit,
                    onClick = { onPress(powerOff) },
                    hapticsEnabled = hapticsEnabled,
                    size = 64.dp,
                    style = KeyStyle.Outlined
                )
            }
        }
    }

    val modesCluster: @Composable () -> Unit = {
        if (modes.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = "Modo de funcionamiento")
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    modes.forEach { mode ->
                        FilterChip(
                            selected = selectedMode == mode.label,
                            onClick = {
                                selectedMode = mode.label
                                onPress(mode)
                            },
                            label = { Text(mode.label, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }
        }
    }

    val fanCluster: @Composable () -> Unit = {
        if (fans.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = "Ventilador")
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fans.forEach { fan ->
                        RemoteKey(
                            label = fan.label,
                            icon = roleIcon(fan.role),
                            onClick = {
                                selectedFan = fan.label
                                onPress(fan)
                            },
                            hapticsEnabled = hapticsEnabled,
                            size = 58.dp,
                            shape = MaterialTheme.shapes.medium,
                            style = if (selectedFan == fan.label) KeyStyle.Filled else KeyStyle.Tonal
                        )
                    }
                }
            }
        }
    }

    val swingCluster: @Composable () -> Unit = {
        if (swings.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = "Oscilación")
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    swings.forEach { swing ->
                        RemoteKey(
                            label = swing.label,
                            icon = roleIcon(swing.role),
                            onClick = { onPress(swing) },
                            hapticsEnabled = hapticsEnabled,
                            size = 58.dp,
                            shape = MaterialTheme.shapes.medium,
                            style = KeyStyle.Tonal
                        )
                    }
                }
            }
        }
    }

    val extrasCluster: @Composable () -> Unit = {
        if (state.extraButtons.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SectionHeader(title = "Otros botones")
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.extraButtons.forEach { button ->
                        RemoteKey(
                            label = button.label,
                            icon = roleIcon(button.role),
                            onClick = { onPress(button) },
                            hapticsEnabled = hapticsEnabled,
                            size = 58.dp,
                            shape = MaterialTheme.shapes.medium,
                            style = KeyStyle.Tonal
                        )
                    }
                }
            }
        }
    }

    if (isWide) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                temperatureCard()
                powerCluster()
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                modesCluster()
                fanCluster()
                swingCluster()
                extrasCluster()
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            temperatureCard()
            powerCluster()
            modesCluster()
            fanCluster()
            swingCluster()
            extrasCluster()
        }
    }
}

private fun buttonOrNull(state: RemoteUiState, role: ButtonRole): RemoteButton? = state.button(role)
