package com.manu.remotecontrol.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.manu.remotecontrol.domain.model.AppSettings
import com.manu.remotecontrol.domain.model.ThemeMode
import com.manu.remotecontrol.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "manu_settings")

/**
 * Ajustes persistidos con DataStore (sin base de datos adicional y sin red).
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object Keys {
        val themeMode = stringPreferencesKey("theme_mode")
        val dynamicColor = booleanPreferencesKey("dynamic_color")
        val carrierFrequency = intPreferencesKey("carrier_frequency_hz")
        val haptics = booleanPreferencesKey("haptics_enabled")
        val forceSimulation = booleanPreferencesKey("force_simulation")
        val showSignalDetails = booleanPreferencesKey("show_signal_details")
        val seeded = booleanPreferencesKey("seed_completed")
    }

    override val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            themeMode = ThemeMode.fromKey(prefs[Keys.themeMode]),
            dynamicColor = prefs[Keys.dynamicColor] ?: true,
            carrierFrequencyOverrideHz = prefs[Keys.carrierFrequency]?.takeIf { it > 0 },
            hapticsEnabled = prefs[Keys.haptics] ?: true,
            forceSimulationMode = prefs[Keys.forceSimulation] ?: false,
            showSignalDetails = prefs[Keys.showSignalDetails] ?: false
        )
    }

    override suspend fun update(transform: (AppSettings) -> AppSettings) {
        val current = settings.first()
        val updated = transform(current)
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.themeMode] = updated.themeMode.storageKey
            prefs[Keys.dynamicColor] = updated.dynamicColor
            prefs[Keys.haptics] = updated.hapticsEnabled
            prefs[Keys.forceSimulation] = updated.forceSimulationMode
            prefs[Keys.showSignalDetails] = updated.showSignalDetails
            updated.carrierFrequencyOverrideHz?.let { prefs[Keys.carrierFrequency] = it }
                ?: run { prefs.remove(Keys.carrierFrequency) }
        }
    }

    override suspend fun setThemeMode(mode: ThemeMode) = update { it.copy(themeMode = mode) }

    override suspend fun setDynamicColor(enabled: Boolean) = update { it.copy(dynamicColor = enabled) }

    override suspend fun setCarrierFrequency(hz: Int?) = update { it.copy(carrierFrequencyOverrideHz = hz) }

    override suspend fun setHapticsEnabled(enabled: Boolean) = update { it.copy(hapticsEnabled = enabled) }

    override suspend fun setForceSimulationMode(enabled: Boolean) = update { it.copy(forceSimulationMode = enabled) }

    suspend fun isSeeded(): Boolean =
        context.settingsDataStore.data.map { it[Keys.seeded] ?: false }.first()

    suspend fun markSeeded() {
        context.settingsDataStore.edit { it[Keys.seeded] = true }
    }
}
