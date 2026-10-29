package com.manu.remotecontrol.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.manu.remotecontrol.data.local.entity.ButtonEntity
import com.manu.remotecontrol.data.local.entity.CatalogButtonEntity
import com.manu.remotecontrol.data.local.entity.CatalogDeviceEntity
import com.manu.remotecontrol.data.local.entity.DeviceEntity
import com.manu.remotecontrol.data.local.entity.DeviceWithButtonCount
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Query(
        """
        SELECT d.*, (SELECT COUNT(*) FROM buttons b WHERE b.deviceId = d.id) AS buttonCount
        FROM devices d
        ORDER BY d.isFavorite DESC, d.createdAt ASC
        """
    )
    fun observeDevices(): Flow<List<DeviceWithButtonCount>>

    @Query(
        """
        SELECT d.*, (SELECT COUNT(*) FROM buttons b WHERE b.deviceId = d.id) AS buttonCount
        FROM devices d WHERE d.id = :deviceId
        """
    )
    fun observeDevice(deviceId: Long): Flow<DeviceWithButtonCount?>

    @Query("SELECT * FROM devices WHERE id = :deviceId")
    suspend fun getDevice(deviceId: Long): DeviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: DeviceEntity): Long

    @Upsert
    suspend fun upsert(device: DeviceEntity): Long

    @Query("UPDATE devices SET isFavorite = :favorite WHERE id = :deviceId")
    suspend fun setFavorite(deviceId: Long, favorite: Boolean)

    @Query("DELETE FROM devices WHERE id = :deviceId")
    suspend fun delete(deviceId: Long)

    @Query("SELECT COUNT(*) FROM devices")
    suspend fun count(): Int
}

@Dao
interface ButtonDao {

    @Query("SELECT * FROM buttons WHERE deviceId = :deviceId ORDER BY sortOrder ASC, id ASC")
    fun observeByDevice(deviceId: Long): Flow<List<ButtonEntity>>

    @Query("SELECT * FROM buttons WHERE deviceId = :deviceId ORDER BY sortOrder ASC, id ASC")
    suspend fun getByDevice(deviceId: Long): List<ButtonEntity>

    @Upsert
    suspend fun upsert(button: ButtonEntity): Long

    @Upsert
    suspend fun upsertAll(buttons: List<ButtonEntity>)

    @Query("DELETE FROM buttons WHERE id = :buttonId")
    suspend fun delete(buttonId: Long)

    @Query("DELETE FROM buttons WHERE deviceId = :deviceId")
    suspend fun deleteByDevice(deviceId: Long)

    @Query("SELECT COUNT(*) FROM buttons")
    suspend fun count(): Int
}

@Dao
interface CatalogDao {

    @Query("SELECT * FROM catalog_devices ORDER BY brand ASC, name ASC")
    fun observeAll(): Flow<List<CatalogDeviceEntity>>

    @Query("SELECT * FROM catalog_devices WHERE type = :type ORDER BY brand ASC, name ASC")
    fun observeByType(type: String): Flow<List<CatalogDeviceEntity>>

    @Query(
        """
        SELECT * FROM catalog_devices
        WHERE (:type IS NULL OR type = :type)
          AND (:query = '' OR name LIKE '%' || :query || '%' COLLATE NOCASE
               OR brand LIKE '%' || :query || '%' COLLATE NOCASE
               OR model LIKE '%' || :query || '%' COLLATE NOCASE)
        ORDER BY brand ASC, name ASC
        """
    )
    suspend fun search(query: String, type: String?): List<CatalogDeviceEntity>

    @Query("SELECT * FROM catalog_devices WHERE catalogKey = :key")
    suspend fun getDevice(key: String): CatalogDeviceEntity?

    @Query("SELECT * FROM catalog_buttons WHERE catalogKey = :key ORDER BY sortOrder ASC")
    suspend fun getButtons(key: String): List<CatalogButtonEntity>

    @Query("SELECT DISTINCT brand FROM catalog_devices WHERE (:type IS NULL OR type = :type) ORDER BY brand ASC")
    suspend fun brands(type: String?): List<String>

    @Query("SELECT COUNT(*) FROM catalog_devices")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<CatalogDeviceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertButtons(buttons: List<CatalogButtonEntity>)

    @Query("DELETE FROM catalog_devices")
    suspend fun clearDevices()
}
