package com.manu.remotecontrol.domain.model

import com.manu.remotecontrol.domain.ir.IrEncoding

/** Rango de frecuencias de portadora que el emisor del teléfono puede generar. */
data class IrFrequencyRange(val minHz: Int, val maxHz: Int) {
    val label: String
        get() = "${minHz / 1000} kHz – ${maxHz / 1000} kHz"

    fun contains(hz: Int): Boolean = hz in minHz..maxHz
}

/**
 * Estado del hardware infrarrojo del teléfono.
 *
 * @property hasPackageFeature el sistema declara `PackageManager.FEATURE_CONSUMER_IR`.
 * @property hasIrEmitter `ConsumerIrManager.hasIrEmitter()` confirma que hay emisor real.
 * @property frequencyRanges rangos reportados por el fabricante (puede venir vacío).
 * @property deviceLabel marca y modelo del teléfono, útil en la pantalla de ajustes.
 */
data class IrHardwareInfo(
    val hasPackageFeature: Boolean,
    val hasIrEmitter: Boolean,
    val frequencyRanges: List<IrFrequencyRange> = emptyList(),
    val deviceLabel: String = "",
    val androidVersion: String = ""
) {
    val isIrAvailable: Boolean get() = hasPackageFeature && hasIrEmitter

    val frequencyRangeLabel: String
        get() = when {
            frequencyRanges.isEmpty() -> "No reportado por el fabricante"
            frequencyRanges.size == 1 -> frequencyRanges.first().label
            else -> frequencyRanges.joinToString(" / ") { it.label }
        }

    val supportsCarrierCalibration: Boolean get() = isIrAvailable

    companion object {
        val Unavailable = IrHardwareInfo(hasPackageFeature = false, hasIrEmitter = false)
    }
}

/** Modo en el que se ha emitido (o simulado) una señal. */
enum class TransmissionMode {
    REAL,
    SIMULATED,
    FAILED
}

/** Resultado de intentar emitir una señal IR. */
data class IrTransmissionResult(
    val mode: TransmissionMode,
    val encoding: IrEncoding? = null,
    val message: String = "",
    val buttonLabel: String = ""
) {
    val wasSent: Boolean get() = mode != TransmissionMode.FAILED
}
