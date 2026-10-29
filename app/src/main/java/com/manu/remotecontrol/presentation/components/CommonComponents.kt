package com.manu.remotecontrol.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoMode
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.FiberManualRecord
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Input
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.SettingsRemote
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.VolumeDown
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.DeviceType
import com.manu.remotecontrol.domain.model.IrHardwareInfo
import com.manu.remotecontrol.domain.model.TransmissionMode
import com.manu.remotecontrol.presentation.theme.LocalManuExtraColors

/** Icono representativo de cada tipo de dispositivo. */
fun deviceTypeIcon(type: DeviceType): ImageVector = when (type) {
    DeviceType.TV -> Icons.Rounded.Tv
    DeviceType.AC -> Icons.Rounded.AcUnit
    DeviceType.DVD -> Icons.Rounded.Album
    DeviceType.AUDIO -> Icons.Rounded.Speaker
    DeviceType.PROJECTOR -> Icons.Rounded.Videocam
    DeviceType.FAN -> Icons.Rounded.Air
    DeviceType.LIGHT -> Icons.Rounded.Lightbulb
    DeviceType.OTHER -> Icons.Rounded.SettingsRemote
}

/** Icono asociado al rol semántico de un botón. */
fun roleIcon(role: ButtonRole): ImageVector = when (role) {
    ButtonRole.POWER, ButtonRole.POWER_ON, ButtonRole.POWER_OFF -> Icons.Rounded.PowerSettingsNew
    ButtonRole.UP -> Icons.Rounded.KeyboardArrowUp
    ButtonRole.DOWN -> Icons.Rounded.KeyboardArrowDown
    ButtonRole.LEFT -> Icons.Rounded.KeyboardArrowLeft
    ButtonRole.RIGHT -> Icons.Rounded.KeyboardArrowRight
    ButtonRole.OK -> Icons.Rounded.Check
    ButtonRole.BACK -> Icons.AutoMirrored.Rounded.ArrowBack
    ButtonRole.HOME -> Icons.Rounded.Home
    ButtonRole.MENU -> Icons.Rounded.Menu
    ButtonRole.EXIT -> Icons.Rounded.Close
    ButtonRole.INFO -> Icons.Rounded.Info
    ButtonRole.INPUT -> Icons.Rounded.Input
    ButtonRole.MUTE -> Icons.Rounded.VolumeOff
    ButtonRole.VOL_UP -> Icons.Rounded.VolumeUp
    ButtonRole.VOL_DOWN -> Icons.Rounded.VolumeDown
    ButtonRole.CH_UP -> Icons.Rounded.Add
    ButtonRole.CH_DOWN -> Icons.Rounded.Remove
    ButtonRole.PLAY -> Icons.Rounded.PlayArrow
    ButtonRole.PAUSE -> Icons.Rounded.Pause
    ButtonRole.STOP -> Icons.Rounded.Stop
    ButtonRole.NEXT -> Icons.Rounded.SkipNext
    ButtonRole.PREVIOUS -> Icons.Rounded.SkipPrevious
    ButtonRole.REWIND -> Icons.Rounded.FastRewind
    ButtonRole.FORWARD -> Icons.Rounded.FastForward
    ButtonRole.RECORD -> Icons.Rounded.FiberManualRecord
    ButtonRole.NETFLIX -> Icons.Rounded.Movie
    ButtonRole.YOUTUBE -> Icons.Rounded.SmartDisplay
    ButtonRole.TEMP_UP -> Icons.Rounded.Add
    ButtonRole.TEMP_DOWN -> Icons.Rounded.Remove
    ButtonRole.MODE -> Icons.Rounded.AutoMode
    ButtonRole.FAN -> Icons.Rounded.Air
    ButtonRole.SWING -> Icons.Rounded.SwapHoriz
    ButtonRole.TIMER -> Icons.Rounded.Timer
    ButtonRole.TURBO -> Icons.Rounded.Bolt
    ButtonRole.SLEEP -> Icons.Rounded.Bedtime
    ButtonRole.CUSTOM -> Icons.Rounded.HelpOutline
    else -> Icons.Rounded.SettingsRemote
}

/** Icono de dispositivo dentro de una caja con degradado. */
@Composable
fun DeviceTypeBadge(
    type: DeviceType,
    modifier: Modifier = Modifier,
    size: Int = 52
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    Box(
        modifier = modifier
            .size(size.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(primary.copy(alpha = 0.95f), secondary.copy(alpha = 0.85f))
                ),
                shape = RoundedCornerShape((size / 3).dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = deviceTypeIcon(type),
            contentDescription = type.displayName,
            tint = Color.White,
            modifier = Modifier.size((size * 0.55f).dp)
        )
    }
}

/** Cabecera de sección con título y subtítulo opcional. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        trailing?.invoke()
    }
}

/**
 * Indicador del emisor IR: un LED que pulsa cada vez que se envía una señal y que cambia
 * de color cuando la emisión es simulada.
 */
@Composable
fun IrSignalIndicator(
    pulse: Long,
    mode: TransmissionMode?,
    modifier: Modifier = Modifier
) {
    val extras = LocalManuExtraColors.current
    val scale = remember { Animatable(1f) }
    val color = when (mode) {
        TransmissionMode.SIMULATED -> extras.simulated
        TransmissionMode.FAILED -> MaterialTheme.colorScheme.error
        else -> extras.irLed
    }

    LaunchedEffect(pulse) {
        if (pulse > 0) {
            scale.snapTo(0.7f)
            scale.animateTo(1.45f, animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing))
            scale.animateTo(1f, animationSpec = tween(durationMillis = 220))
        }
    }

    Box(modifier = modifier.size(36.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .scale(scale.value)
                .background(color.copy(alpha = 0.18f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
    }
}

/** Tarjeta compacta con el estado del hardware IR. */
@Composable
fun IrStatusCard(
    hardware: IrHardwareInfo,
    simulationForced: Boolean,
    modifier: Modifier = Modifier
) {
    val extras = LocalManuExtraColors.current
    val isReady = hardware.isIrAvailable && !simulationForced
    val container = when {
        isReady -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor = when {
        isReady -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onTertiaryContainer
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = container,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IrSignalIndicator(pulse = 0L, mode = if (isReady) TransmissionMode.REAL else TransmissionMode.SIMULATED)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isReady) "Emisor IR listo" else "Modo Simulación",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = when {
                        simulationForced && hardware.isIrAvailable ->
                            "Forzado en Ajustes · ${hardware.frequencyRangeLabel}"
                        hardware.isIrAvailable -> hardware.frequencyRangeLabel
                        hardware.hasIrEmitter -> "El sistema reporta emisor, pero no responde"
                        else -> "Este teléfono no tiene emisor IR · interfaz navegable"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!isReady) {
                Box(
                    modifier = Modifier
                        .background(extras.simulated.copy(alpha = 0.18f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("SIM", style = MaterialTheme.typography.labelSmall, color = extras.simulated)
                }
            }
        }
    }
}

/** Fila de etiqueta y valor usada en ajustes y fichas técnicas. */
@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
