package com.manu.remotecontrol.data.local.seed

import com.manu.remotecontrol.domain.ir.IrCode
import com.manu.remotecontrol.domain.model.ButtonRole
import com.manu.remotecontrol.domain.model.ButtonSection
import com.manu.remotecontrol.domain.model.DeviceType

/** Botón de una entrada del catálogo precargado. */
internal data class SeedButton(
    val label: String,
    val role: ButtonRole,
    val section: ButtonSection,
    val code: IrCode
)

/** Dispositivo del catálogo precargado, listo para copiarse a la base Room. */
internal data class SeedDevice(
    val key: String,
    val name: String,
    val brand: String,
    val model: String,
    val type: DeviceType,
    val frequencyHz: Int,
    val description: String,
    val source: String,
    val buttons: List<SeedButton>
)

/** Azúcar sintáctico para declarar botones en la semilla. */
internal fun btn(label: String, role: ButtonRole, section: ButtonSection, code: IrCode): SeedButton =
    SeedButton(label = label, role = role, section = section, code = code)

/**
 * Construye un [IrCode.Raw] a partir de los microsegundos en texto (fragmentos concatenados
 * para que las tramas largas de los aires acondicionados sigan siendo legibles).
 */
internal fun raw(frequencyHz: Int, vararg chunks: String): IrCode.Raw {
    val pattern = chunks.joinToString(" ")
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .map { it.toInt() }
    return IrCode.Raw(pattern = pattern, frequencyHz = frequencyHz)
}

/**
 * Catálogo offline completo que se siembra en Room la primera vez que arranca la app.
 *
 * Todo el contenido es local: no se descarga nada ni se consulta ninguna API.
 */
internal object SeedCatalog {

    val devices: List<SeedDevice> = SeedTv.devices + SeedAc.devices + SeedDvd.devices

    /** Dispositivos que se crean ya configurados en el inicio para poder probar la app. */
    val sampleDeviceKeys: List<String> = listOf("tv_samsung", "ac_lg", "dvd_sony")

    fun byKey(key: String): SeedDevice? = devices.firstOrNull { it.key == key }
}
