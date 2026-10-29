package com.manu.remotecontrol.core.di

import android.content.Context
import androidx.room.Room
import com.manu.remotecontrol.data.ir.AndroidIrHardwareRepository
import com.manu.remotecontrol.data.ir.ConsumerIrTransmitter
import com.manu.remotecontrol.data.local.ManuDatabase
import com.manu.remotecontrol.data.local.SettingsRepositoryImpl
import com.manu.remotecontrol.data.local.dao.ButtonDao
import com.manu.remotecontrol.data.local.dao.CatalogDao
import com.manu.remotecontrol.data.local.dao.DeviceDao
import com.manu.remotecontrol.data.repository.CatalogRepositoryImpl
import com.manu.remotecontrol.data.repository.DeviceRepositoryImpl
import com.manu.remotecontrol.domain.ir.IrEncoders
import com.manu.remotecontrol.domain.ir.IrProtocolRegistry
import com.manu.remotecontrol.domain.repository.CatalogRepository
import com.manu.remotecontrol.domain.repository.DeviceRepository
import com.manu.remotecontrol.domain.repository.IrHardwareRepository
import com.manu.remotecontrol.domain.repository.IrTransmitter
import com.manu.remotecontrol.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Base de datos Room y sus DAO. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ManuDatabase =
        Room.databaseBuilder(context, ManuDatabase::class.java, ManuDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDeviceDao(database: ManuDatabase): DeviceDao = database.deviceDao()

    @Provides
    fun provideButtonDao(database: ManuDatabase): ButtonDao = database.buttonDao()

    @Provides
    fun provideCatalogDao(database: ManuDatabase): CatalogDao = database.catalogDao()
}

/**
 * Motor de protocolos IR y emisor.
 *
 * El registro se construye a partir de [IrEncoders.defaultEncoders]; para añadir un
 * protocolo nuevo se añade a esa lista y no hay que tocar nada más.
 */
@Module
@InstallIn(SingletonComponent::class)
object IrModule {

    @Provides
    @Singleton
    fun provideIrProtocolRegistry(): IrProtocolRegistry = IrEncoders.defaultRegistry()
}

/** Implementaciones de repositorios. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(impl: CatalogRepositoryImpl): CatalogRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindHardwareRepository(impl: AndroidIrHardwareRepository): IrHardwareRepository

    @Binds
    @Singleton
    abstract fun bindIrTransmitter(impl: ConsumerIrTransmitter): IrTransmitter
}
