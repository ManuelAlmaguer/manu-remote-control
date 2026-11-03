package com.manu.remotecontrol.data.local.seed

import com.manu.remotecontrol.data.local.dao.ButtonDao
import com.manu.remotecontrol.data.local.dao.CatalogDao
import com.manu.remotecontrol.data.local.dao.DeviceDao
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.data.local.entity.CatalogButtonEntity
import com.manu.remotecontrol.data.local.entity.CatalogDeviceEntity
import com.manu.remotecontrol.data.local.entity.DeviceEntity
import com.manu.remotecontrol.data.local.SettingsRepositoryImpl
import com.manu.remotecontrol.data.mapper.toCatalogEntity
import com.manu.remotecontrol.data.mapper.toEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Siembra la base de datos local la primera vez que arranca la app:
 *
 * 1. Copia el catálogo precargado en Kotlin (mandos de referencia con etiquetas en español).
 * 2. Importa el catálogo extendido del asset (`assets/catalog/manu_catalog.tsv.gz`): cientos
 *    de marcas y miles de mandos reales de Flipper-IRDB.
 * 3. Crea dos o tres dispositivos de ejemplo ya listos para probar el control.
 *
 * Es idempotente y está protegido con un [Mutex] para que dos pantallas no siembren a la vez.
 * Cuando cambia [AssetCatalog.VERSION] el catálogo extendido se vuelve a importar solo.
 */
@Singleton
class AppSeeder @Inject constructor(
    private val deviceDao: DeviceDao,
    private val buttonDao: ButtonDao,
    private val catalogDao: CatalogDao,
    private val settingsRepository: SettingsRepositoryImpl,
    private val catalogAssetLoader: CatalogAssetLoader,
    @ApplicationContext private val context: Context
) {

    private val mutex = Mutex()
    private var seededInMemory = false

    suspend fun seedIfNeeded() {
        if (seededInMemory) return
        mutex.withLock {
            if (seededInMemory) return
            if (!settingsRepository.isSeeded()) {
                seedCatalog()
                importAssetCatalog()
                seedSampleDevices()
                settingsRepository.markSeeded()
                seededInMemory = true
                return
            }
            if (catalogDao.count() == 0) {
                // Catálogo vacío (por ejemplo tras limpiar datos): se vuelve a sembrar.
                seedCatalog()
                importAssetCatalog()
            } else if (settingsRepository.catalogVersion() < AssetCatalog.VERSION) {
                // Nueva versión del catálogo incluida en la actualización de la app.
                catalogDao.clearButtons()
                catalogDao.clearDevices()
                seedCatalog()
                importAssetCatalog()
            }
            seededInMemory = true
        }
    }

    /** Importa el catálogo extendido que viaja en el APK y anota la versión aplicada. */
    private suspend fun importAssetCatalog() {
        if (!assetExists()) return
        runCatching { catalogAssetLoader.importInto(catalogDao) }
            .onSuccess { settingsRepository.setCatalogVersion(AssetCatalog.VERSION) }
    }

    private fun assetExists(): Boolean = runCatching {
        context.assets.open(AssetCatalog.PATH).close()
        true
    }.getOrDefault(false)

    private suspend fun seedCatalog() {
        val deviceEntities = mutableListOf<CatalogDeviceEntity>()
        val buttonEntities = mutableListOf<CatalogButtonEntity>()
        SeedCatalog.devices.forEach { seed ->
            deviceEntities += seed.toCatalogEntity()
            seed.buttons.forEachIndexed { index, button ->
                buttonEntities += button.toCatalogEntity(seed.key, index)
            }
        }
        catalogDao.insertDevices(deviceEntities)
        catalogDao.insertButtons(buttonEntities)
    }

    private suspend fun seedSampleDevices() {
        if (deviceDao.count() > 0) return
        SeedCatalog.sampleDeviceKeys.forEachIndexed { index, key ->
            val seed = SeedCatalog.byKey(key) ?: return@forEachIndexed
            val deviceId = deviceDao.insert(
                DeviceEntity(
                    name = seed.name,
                    brand = seed.brand,
                    model = seed.model,
                    type = seed.type.storageKey,
                    isFavorite = index == 0,
                    isCustom = false,
                    carrierFrequencyHz = seed.frequencyHz,
                    createdAt = System.currentTimeMillis() + index
                )
            )
            buttonDao.upsertAll(
                seed.buttons.mapIndexed { order, button ->
                    RemoteButton(
                        deviceId = deviceId,
                        label = button.label,
                        role = button.role,
                        section = button.section,
                        code = button.code,
                        sortOrder = order
                    ).toEntity()
                }
            )
        }
    }

}
