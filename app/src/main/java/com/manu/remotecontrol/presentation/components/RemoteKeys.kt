package com.manu.remotecontrol.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manu.remotecontrol.presentation.theme.LocalManuExtraColors

/** Estilos visuales de las teclas del control. */
enum class KeyStyle {
    Filled,
    Tonal,
    Outlined,
    Danger,
    Ghost
}

/** Resuelve los colores de una tecla según su estilo. */
@Composable
private fun keyColors(style: KeyStyle): Pair<Color, Color> = when (style) {
    KeyStyle.Filled -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    KeyStyle.Tonal -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface
    KeyStyle.Outlined -> Color.Transparent to MaterialTheme.colorScheme.onSurface
    KeyStyle.Danger -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    KeyStyle.Ghost -> Color.Transparent to MaterialTheme.colorScheme.onSurfaceVariant
}

/**
 * Tecla genérica del control: superficie táctil con onda (ripple), animación de pulsación
 * y vibración háptica ligera para imitar un botón físico.
 */
@Composable
fun RemoteKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    style: KeyStyle = KeyStyle.Tonal,
    shape: Shape = CircleShape,
    size: Dp = 62.dp,
    enabled: Boolean = true,
    hapticsEnabled: Boolean = true,
    showLabel: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.93f else 1f, label = "keyScale")
    val (container, content) = keyColors(style)
    val haptics = LocalHapticFeedback.current

    Surface(
        onClick = {
            if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        enabled = enabled,
        modifier = modifier
            .size(size)
            .scale(scale),
        shape = shape,
        color = container,
        contentColor = content,
        border = if (style == KeyStyle.Outlined) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        } else {
            null
        },
        tonalElevation = if (style == KeyStyle.Tonal) 1.dp else 0.dp,
        shadowElevation = if (style == KeyStyle.Filled) 2.dp else 0.dp,
        interactionSource = interactionSource
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(size * 0.36f)
                    )
                }
                if (showLabel && label.isNotBlank()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = if (icon != null) 9.sp else 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.padding(top = if (icon != null) 2.dp else 0.dp)
                    )
                }
            }
        }
    }
}

/** Botón de encendido destacado en rojo. */
@Composable
fun PowerKey(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Encendido",
    hapticsEnabled: Boolean = true,
    size: Dp = 74.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "powerScale")
    val extras = LocalManuExtraColors.current
    val haptics = LocalHapticFeedback.current

    Surface(
        onClick = {
            if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .size(size)
            .scale(scale),
        shape = CircleShape,
        color = extras.powerRed,
        contentColor = Color.White,
        shadowElevation = 6.dp,
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.PowerSettingsNew,
                contentDescription = label,
                modifier = Modifier.size(size * 0.38f)
            )
        }
    }
}

/** D-Pad ergonómico con OK central. */
@Composable
fun DPad(
    onUp: () -> Unit,
    onDown: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onOk: () -> Unit,
    modifier: Modifier = Modifier,
    hapticsEnabled: Boolean = true,
    keySize: Dp = 60.dp
) {
    Box(modifier = modifier.size(keySize * 3.4f)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(keySize * 0.42f)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f), CircleShape)
        )
        RemoteKey(
            label = "Arriba",
            icon = Icons.Rounded.KeyboardArrowUp,
            showLabel = false,
            onClick = onUp,
            hapticsEnabled = hapticsEnabled,
            size = keySize,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        RemoteKey(
            label = "Abajo",
            icon = Icons.Rounded.KeyboardArrowDown,
            showLabel = false,
            onClick = onDown,
            hapticsEnabled = hapticsEnabled,
            size = keySize,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        RemoteKey(
            label = "Izquierda",
            icon = Icons.Rounded.KeyboardArrowLeft,
            showLabel = false,
            onClick = onLeft,
            hapticsEnabled = hapticsEnabled,
            size = keySize,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        RemoteKey(
            label = "Derecha",
            icon = Icons.Rounded.KeyboardArrowRight,
            showLabel = false,
            onClick = onRight,
            hapticsEnabled = hapticsEnabled,
            size = keySize,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        RemoteKey(
            label = "OK",
            onClick = onOk,
            style = KeyStyle.Filled,
            hapticsEnabled = hapticsEnabled,
            size = keySize,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/** Balancín vertical (volumen o canal) con dos pulsadores. */
@Composable
fun RockerKey(
    topLabel: String,
    bottomLabel: String,
    onTop: () -> Unit,
    onBottom: () -> Unit,
    modifier: Modifier = Modifier,
    hapticsEnabled: Boolean = true,
    width: Dp = 64.dp,
    height: Dp = 116.dp
) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = modifier
            .width(width)
            .height(height)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(32.dp))
    ) {
        Surface(
            onClick = {
                if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onTop()
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = topLabel,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        )
        Surface(
            onClick = {
                if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onBottom()
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = bottomLabel,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Teclado numérico 3x4 con 0 y borrado visual (sin acción si no hay código). */
@Composable
fun NumericKeypad(
    onDigit: (Int) -> Unit,
    modifier: Modifier = Modifier,
    hapticsEnabled: Boolean = true,
    keySize: Dp = 58.dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        (1..9).chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { digit ->
                    RemoteKey(
                        label = digit.toString(),
                        onClick = { onDigit(digit) },
                        hapticsEnabled = hapticsEnabled,
                        size = keySize
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Spacer(modifier = Modifier.size(keySize))
            RemoteKey(
                label = "0",
                onClick = { onDigit(0) },
                hapticsEnabled = hapticsEnabled,
                size = keySize
            )
            Spacer(modifier = Modifier.size(keySize))
        }
    }
}
