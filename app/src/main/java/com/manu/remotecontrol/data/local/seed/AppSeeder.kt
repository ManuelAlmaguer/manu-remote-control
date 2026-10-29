package com.manu.remotecontrol.data.local.seed

import com.manu.remotecontrol.data.local.dao.ButtonDao
import com.manu.remotecontrol.data.local.dao.CatalogDao
import com.manu.remotecontrol.data.local.dao.DeviceDao
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
 * 1. Copia el catálogo precargado (13 mandos reales, ~250 botones) a las tablas `catalog_*`.
 * 2. Crea dos o tres dispositivos de ejemplo ya listos para probar el control.
 *
 * Es idempotente y está protegido con un [Mutex] para que dos pantallas no siembren a la vez.
 */
@Singleton
class AppSeeder @Inject constructor(
    private val deviceDao: DeviceDao,
    private val buttonDao: ButtonDao,
    private val catalogDao: CatalogDao,
    private val settingsRepository: SettingsRepositoryImpl
) {

    private val mutex = Mutex()
    private var seededInMemory = false

    suspend fun seedIfNeeded() {
        if (seededInMemory) return
        mutex.withLock {
            if (seededInMemory) return
            if (!settingsRepository.isSeeded()) {
                seedCatalog()
                seedSampleDevices()
                settingsRepository.markSeeded()
            } else if (catalogDao.count() == 0) {
                // Catálogo vacío (por ejemplo tras limpiar datos): se vuelve a sembrar.
                seedCatalog()
            }
            seededInMemory = true
        }
    }

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
