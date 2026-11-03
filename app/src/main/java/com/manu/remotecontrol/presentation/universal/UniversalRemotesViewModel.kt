package com.manu.remotecontrol.presentation.universal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manu.remotecontrol.data.local.universal.UniversalRemote
import com.manu.remotecontrol.data.local.universal.UniversalRemoteCatalog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Estado de la pantalla de mandos universales. */
data class UniversalUiState(
    val isLoading: Boolean = true,
    val remotes: List<UniversalRemote> = emptyList(),
    val selectedId: String? = null,
    val query: String = ""
) {
    val selected: UniversalRemote?
        get() = remotes.firstOrNull { it.id == selectedId } ?: remotes.firstOrNull()
}

@HiltViewModel
class UniversalRemotesViewModel @Inject constructor(
    private val catalog: UniversalRemoteCatalog
) : ViewModel() {

    private val _state = MutableStateFlow(UniversalUiState())
    val state: StateFlow<UniversalUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val remotes = catalog.load()
            _state.update {
                it.copy(
                    isLoading = false,
                    remotes = remotes,
                    selectedId = it.selectedId ?: remotes.firstOrNull()?.id
                )
            }
        }
    }

    fun onSelect(remoteId: String) = _state.update { it.copy(selectedId = remoteId) }

    fun onQueryChange(query: String) = _state.update { it.copy(query = query) }
}
