package com.musicplayer.pro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicplayer.pro.data.database.MusicDatabase
import com.musicplayer.pro.data.model.Song
import com.musicplayer.pro.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SongListUiState(
    val songs: List<Song> = emptyList(),
    val filteredSongs: List<Song> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class SongListViewModel(context: Context) : ViewModel() {
    private val database = MusicDatabase.getInstance(context)
    private val repository = SongRepository(context, database)

    private val _uiState = MutableStateFlow(SongListUiState())
    val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.loadSongsFromMediaStore()
                repository.getAllSongs().collect { songs ->
                    _uiState.update { state ->
                        state.copy(
                            songs = songs,
                            filteredSongs = songs,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun searchSongs(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isEmpty()) {
            _uiState.update { it.copy(filteredSongs = it.songs) }
        } else {
            viewModelScope.launch {
                repository.searchSongs("%$query%").collect { songs ->
                    _uiState.update { it.copy(filteredSongs = songs) }
                }
            }
        }
    }

    fun refreshSongs() {
        loadSongs()
    }
}
