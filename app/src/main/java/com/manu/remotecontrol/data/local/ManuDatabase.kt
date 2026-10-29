package com.manu.remotecontrol.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.manu.remotecontrol.data.local.dao.ButtonDao
import com.manu.remotecontrol.data.local.dao.CatalogDao
import com.manu.remotecontrol.data.local.dao.DeviceDao
import com.manu.remotecontrol.data.local.entity.ButtonEntity
import com.manu.remotecontrol.data.local.entity.CatalogButtonEntity
import com.manu.remotecontrol.data.local.entity.CatalogDeviceEntity
import com.manu.remotecontrol.data.local.entity.DeviceEntity

/**
 * Base de datos local (Room + KSP). Es la única fuente de verdad de la app: todo el
 * contenido funciona sin conexión y sobrevive al cierre de la aplicación.
 */
@Database(
    entities = [
        DeviceEntity::class,
        ButtonEntity::class,
        CatalogDeviceEntity::class,
        CatalogButtonEntity::class
    ],
    version = ManuDatabase.VERSION,
    exportSchema = true
)
abstract class ManuDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

    abstract fun buttonDao(): ButtonDao

    abstract fun catalogDao(): CatalogDao

    companion object {
        const val VERSION = 1
        const val NAME = "manu-remote-control.db"
    }
}
