package com.manu.remotecontrol.data.local.seed

import android.content.Context
import com.manu.remotecontrol.data.local.dao.CatalogDao
import com.manu.remotecontrol.data.local.entity.CatalogButtonEntity
import com.manu.remotecontrol.data.local.entity.CatalogDeviceEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Importador del catálogo extendido que viaja dentro del APK
 * (`assets/catalog/manu_catalog.tsv.gz`).
 *
 * El asset lo genera `tools/ir_catalog/build_catalog.py` a partir de Flipper-IRDB (CC0) y
 * contiene cientos de marcas y miles de mandos reales. Se lee en streaming y se inserta
 * en Room por lotes para que el primer arranque no bloquee la interfaz ni consuma memoria
 * de más.
 */
@Singleton
class CatalogAssetLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /** Importa el asset completo. Es idempotente: las claves del catálogo se sobrescriben. */
    suspend fun importInto(catalogDao: CatalogDao): Int {
        var imported = 0
        context.assets.open(AssetCatalog.PATH).use { raw ->
            GZIPInputStream(raw).bufferedReader(Charsets.UTF_8).use { reader ->
                val pendingDevices = mutableListOf<CatalogDeviceEntity>()
                val pendingButtons = mutableListOf<CatalogButtonEntity>()
                var currentDevice: CatalogDeviceEntity? = null
                var currentButtons = mutableListOf<CatalogButtonEntity>()
                var sortOrder = 0

                suspend fun flushCurrent() {
                    val device = currentDevice ?: return
                    pendingDevices += device.copy(buttonCount = currentButtons.size)
                    pendingButtons += currentButtons
                    imported += 1
                    currentDevice = null
                    currentButtons = mutableListOf()
                    sortOrder = 0
                    if (pendingButtons.size >= BUTTON_BATCH) {
                        writeBatches(catalogDao, pendingDevices, pendingButtons)
                    }
                }

                val header = reader.readLine()
                require(header == AssetCatalog.HEADER) {
                    "Formato de catálogo no soportado: ${header ?: "vacío"}"
                }

                while (true) {
                    val line = reader.readLine() ?: break
                    if (line.isEmpty()) continue
                    val fields = line.split('\t')
                    when (fields.first()) {
                        ROW_DEVICE -> {
                            flushCurrent()
                            parseDevice(fields)?.let { currentDevice = it }
                        }

                        ROW_BUTTON -> {
                            val device = currentDevice ?: continue
                            parseButton(fields, device.catalogKey, sortOrder)?.let { button ->
                                currentButtons += button
                                sortOrder += 1
                            }
                        }
                    }
                }
                flushCurrent()
                writeBatches(catalogDao, pendingDevices, pendingButtons)
            }
        }
        return imported
    }

    private suspend fun writeBatches(
        catalogDao: CatalogDao,
        devices: MutableList<CatalogDeviceEntity>,
        buttons: MutableList<CatalogButtonEntity>
    ) {
        // Primero los dispositivos: los botones tienen clave foránea a la cabecera.
        if (devices.isNotEmpty()) {
            catalogDao.insertDevices(devices.toList())
            devices.clear()
        }
        if (buttons.isNotEmpty()) {
            buttons.chunked(BUTTON_BATCH).forEach { catalogDao.insertButtons(it) }
            buttons.clear()
        }
    }

    private fun parseDevice(fields: List<String>): CatalogDeviceEntity? {
        if (fields.size < 9) return null
        return CatalogDeviceEntity(
            catalogKey = fields[1],
            name = fields[2],
            brand = fields[3],
            model = fields[4],
            type = fields[5],
            frequencyHz = fields[6].toIntOrNull()?.takeIf { it > 0 } ?: 38_000,
            description = fields[7],
            source = fields[8],
            buttonCount = 0
        )
    }

    private fun parseButton(
        fields: List<String>,
        catalogKey: String,
        sortOrder: Int
    ): CatalogButtonEntity? {
        if (fields.size < 12) return null
        val protocol = fields[5]
        if (protocol !in AssetCatalog.KNOWN_PROTOCOLS) return null
        val rawPattern = fields.getOrNull(12)?.takeIf { it.isNotEmpty() }
        if (protocol == "RAW" && rawPattern == null) return null
        return CatalogButtonEntity(
            catalogKey = catalogKey,
            label = fields[2],
            role = fields[3],
            section = fields[4],
            protocol = protocol,
            frequencyHz = fields[6].toIntOrNull()?.takeIf { it > 0 } ?: 38_000,
            address = fields[7].toIntOrNull() ?: 0,
            command = fields[8].toIntOrNull() ?: 0,
            bits = fields[9].toIntOrNull() ?: 12,
            toggle = fields[10] == "1",
            repeats = fields[11].toIntOrNull()?.coerceAtLeast(1) ?: 1,
            rawPattern = rawPattern,
            sortOrder = sortOrder
        )
    }

    private companion object {
        const val ROW_DEVICE = "D"
        const val ROW_BUTTON = "B"
        const val BUTTON_BATCH = 2_000
    }
}

/** Metadatos del asset de catálogo extendido. */
object AssetCatalog {

    /** Se incrementa al regenerar el asset para forzar la reimportación en equipos ya instalados. */
    const val VERSION = 1

    const val PATH = "catalog/manu_catalog.tsv.gz"

    const val HEADER = "MU1"

    val KNOWN_PROTOCOLS = setOf(
        "NEC",
        "NEC_EXTENDED",
        "SAMSUNG",
        "SONY_SIRC",
        "RC5",
        "KASEIKYO",
        "PIONEER",
        "RAW"
    )
}
