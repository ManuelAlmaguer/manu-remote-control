package com.manu.remotecontrol.data.repository

import com.manu.remotecontrol.data.local.dao.ButtonDao
import com.manu.remotecontrol.data.local.dao.CatalogDao
import com.manu.remotecontrol.data.local.dao.DeviceDao
import com.manu.remotecontrol.data.local.entity.DeviceEntity
import com.manu.remotecontrol.data.local.seed.AppSeeder
import com.manu.remotecontrol.data.mapper.toDomain
import com.manu.remotecontrol.data.mapper.toEntity
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.domain.model.RemoteDevice
import com.manu.remotecontrol.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio offline-first: Room es la única fuente de verdad. Antes de cada consulta
 * se asegura de que la semilla local esté cargada.
 */
@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao,
    private val buttonDao: ButtonDao,
    private val catalogDao: CatalogDao,
    private val seeder: AppSeeder
) : DeviceRepository {

    override fun observeDevices(): Flow<List<RemoteDevice>> = flow {
        seeder.seedIfNeeded()
        emitAll(deviceDao.observeDevices())
    }.map { rows -> rows.map { it.toDomain() } }

    override fun observeDevice(deviceId: Long): Flow<RemoteDevice?> = flow {
        seeder.seedIfNeeded()
        emitAll(deviceDao.observeDevice(deviceId))
    }.map { it?.toDomain() }

    override fun observeButtons(deviceId: Long): Flow<List<RemoteButton>> =
        buttonDao.observeByDevice(deviceId).map { buttons -> buttons.map { it.toDomain() } }

    override suspend fun getDevice(deviceId: Long): RemoteDevice? =
        deviceDao.getDevice(deviceId)?.toDomain()

    override suspend fun getButtons(deviceId: Long): List<RemoteButton> =
        buttonDao.getByDevice(deviceId).map { it.toDomain() }

    override suspend fun saveDevice(device: RemoteDevice): Long {
        val entity = device.toEntity()
        return if (entity.id == 0L) {
            deviceDao.insert(entity)
        } else {
            deviceDao.upsert(entity)
            entity.id
        }
    }

    override suspend fun deleteDevice(deviceId: Long) = deviceDao.delete(deviceId)

    override suspend fun setFavorite(deviceId: Long, favorite: Boolean) =
        deviceDao.setFavorite(deviceId, favorite)

    override suspend fun saveButton(button: RemoteButton): Long = buttonDao.upsert(button.toEntity())

    override suspend fun saveButtons(buttons: List<RemoteButton>) =
        buttonDao.upsertAll(buttons.map { it.toEntity() })

    override suspend fun deleteButton(buttonId: Long) = buttonDao.delete(buttonId)

    override suspend fun createFromCatalog(catalogKey: String, customName: String?): Long? {
        val catalog = catalogDao.getDevice(catalogKey) ?: return null
        val catalogButtons = catalogDao.getButtons(catalogKey)
        val deviceId = deviceDao.insert(
            DeviceEntity(
                name = customName?.takeIf { it.isNotBlank() } ?: catalog.name,
                brand = catalog.brand,
                model = catalog.model,
                type = catalog.type,
                isCustom = false,
                carrierFrequencyHz = catalog.frequencyHz
            )
        )
        buttonDao.upsertAll(catalogButtons.map { it.toDomain(deviceId).toEntity() })
        return deviceId
    }
}
