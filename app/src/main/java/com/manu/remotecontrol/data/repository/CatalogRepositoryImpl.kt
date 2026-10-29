package com.manu.remotecontrol.data.repository

import com.manu.remotecontrol.data.local.dao.CatalogDao
import com.manu.remotecontrol.data.local.seed.AppSeeder
import com.manu.remotecontrol.data.mapper.toDomain
import com.manu.remotecontrol.domain.model.CatalogDevice
import com.manu.remotecontrol.domain.model.RemoteButton
import com.manu.remotecontrol.domain.repository.CatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepositoryImpl @Inject constructor(
    private val catalogDao: CatalogDao,
    private val seeder: AppSeeder
) : CatalogRepository {

    override fun observeCatalog(type: String?): Flow<List<CatalogDevice>> = flow {
        seeder.seedIfNeeded()
        emitAll(if (type == null) catalogDao.observeAll() else catalogDao.observeByType(type))
    }.map { list -> list.map { it.toDomain() } }

    override suspend fun search(query: String, type: String?): List<CatalogDevice> {
        seeder.seedIfNeeded()
        return catalogDao.search(query.trim(), type).map { it.toDomain() }
    }

    override suspend fun getCatalogDevice(key: String): CatalogDevice? {
        seeder.seedIfNeeded()
        return catalogDao.getDevice(key)?.toDomain()
    }

    override suspend fun getCatalogButtons(key: String): List<RemoteButton> {
        seeder.seedIfNeeded()
        return catalogDao.getButtons(key).map { it.toDomain(deviceId = 0L) }
    }

    override suspend fun brands(type: String?): List<String> {
        seeder.seedIfNeeded()
        return catalogDao.brands(type)
    }
}
