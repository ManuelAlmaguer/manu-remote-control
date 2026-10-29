package com.manu.remotecontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manu.remotecontrol.domain.model.AppSettings
import com.manu.remotecontrol.domain.usecase.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Mantiene los ajustes de tema disponibles para toda la jerarquía de Compose. */
@HiltViewModel
class MainViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<AppSettings> = observeSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )
}
