package com.manu.remotecontrol.data.ir

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.ConsumerIrManager
import android.os.Build
import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.ir.IrEncoding
import com.manu.remotecontrol.domain.ir.IrProtocolRegistry
import com.manu.remotecontrol.domain.model.IrFrequencyRange
import com.manu.remotecontrol.domain.model.IrHardwareInfo
import com.manu.remotecontrol.domain.model.IrTransmissionResult
import com.manu.remotecontrol.domain.model.TransmissionMode
import com.manu.remotecontrol.domain.repository.IrHardwareRepository
import com.manu.remotecontrol.domain.repository.IrTransmitter
import com.manu.remotecontrol.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación real de [IrHardwareRepository] sobre `ConsumerIrManager`.
 *
 * Se comprueba primero la feature declarada por el sistema
 * (`PackageManager.FEATURE_CONSUMER_IR`) y después `hasIrEmitter()`, que es la fuente de
 * verdad. Si el fabricante expone rangos de frecuencia, se muestran en Ajustes.
 */
@Singleton
class AndroidIrHardwareRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IrHardwareRepository {

    private val irManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    override suspend fun getHardwareInfo(): IrHardwareInfo = withContext(Dispatchers.Default) {
        val hasFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR)
        val hasEmitter = try {
            irManager?.hasIrEmitter() == true
        } catch (security: SecurityException) {
            false
        } catch (unexpected: Throwable) {
            false
        }

        val ranges = if (hasEmitter) readFrequencyRanges() else emptyList()

        IrHardwareInfo(
            hasPackageFeature = hasFeature,
            hasIrEmitter = hasEmitter,
            frequencyRanges = ranges,
            deviceLabel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}",
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        )
    }

    override fun observeHardwareInfo(): Flow<IrHardwareInfo> = flow {
        emit(getHardwareInfo())
    }.flowOn(Dispatchers.Default)

    private fun readFrequencyRanges(): List<IrFrequencyRange> = try {
        irManager?.carrierFrequencies
            ?.map { IrFrequencyRange(it.minFrequency, it.maxFrequency) }
            ?.sortedBy { it.minHz }
            .orEmpty()
    } catch (security: SecurityException) {
        emptyList()
    } catch (unexpected: Throwable) {
        emptyList()
    }
}

/**
 * Transmisor IR. Usa `ConsumerIrManager.transmit()` cuando hay hardware y, si no lo hay
 * (o el usuario fuerza el modo), devuelve una emisión simulada para que la interfaz siga
 * siendo completamente navegable y testeable.
 */
@Singleton
class ConsumerIrTransmitter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val registry: IrProtocolRegistry,
    private val settingsRepository: SettingsRepository
) : IrTransmitter {

    private val irManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    override suspend fun isHardwareAvailable(): Boolean = withContext(Dispatchers.Default) {
        val hasFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR)
        val hasEmitter = try {
            irManager?.hasIrEmitter() == true
        } catch (t: Throwable) {
            false
        }
        hasFeature && hasEmitter
    }

    override suspend fun transmit(code: IrCode, carrierFrequencyHz: Int?): IrTransmissionResult {
        val settings = settingsRepository.settings.first()

        val encoding: IrEncoding = try {
            registry.encode(code, carrierFrequencyHz ?: settings.carrierFrequencyOverrideHz)
        } catch (error: Exception) {
            return IrTransmissionResult(
                mode = TransmissionMode.FAILED,
                message = "No se pudo codificar la señal: ${error.message}"
            )
        }

        val hardwareReady = isHardwareAvailable()

        if (!hardwareReady || settings.forceSimulationMode) {
            val reason = if (!hardwareReady) {
                "Sin emisor IR en este dispositivo"
            } else {
                "Modo Simulación activado en Ajustes"
            }
            return IrTransmissionResult(
                mode = TransmissionMode.SIMULATED,
                encoding = encoding,
                message = "$reason · señal simulada a ${encoding.frequencyHz / 1000} kHz"
            )
        }

        val manager = irManager
            ?: return IrTransmissionResult(
                mode = TransmissionMode.SIMULATED,
                encoding = encoding,
                message = "ConsumerIrManager no disponible · señal simulada"
            )

        return try {
            manager.transmit(encoding.frequencyHz, encoding.pattern)
            IrTransmissionResult(
                mode = TransmissionMode.REAL,
                encoding = encoding,
                message = "Enviado · ${encoding.frequencyHz / 1000} kHz · ${encoding.pulseCount} pulsos"
            )
        } catch (security: SecurityException) {
            IrTransmissionResult(
                mode = TransmissionMode.FAILED,
                encoding = encoding,
                message = "El sistema bloqueó el emisor IR (permiso TRANSMIT_IR)"
            )
        } catch (error: Exception) {
            IrTransmissionResult(
                mode = TransmissionMode.FAILED,
                encoding = encoding,
                message = "Error al emitir: ${error.message ?: "desconocido"}"
            )
        }
    }
}
