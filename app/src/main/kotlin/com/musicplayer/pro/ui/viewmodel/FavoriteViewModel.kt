package com.musicplayer.pro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicplayer.pro.data.database.MusicDatabase
import com.musicplayer.pro.data.model.Song
import com.musicplayer.pro.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val favoriteSongs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class FavoriteViewModel(context: Context) : ViewModel() {
    private val database = MusicDatabase.getInstance(context)
    private val repository = FavoriteRepository(database)

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavoriteSongs().collect { songs ->
                _uiState.update { it.copy(favoriteSongs = songs) }
            }
        }
    }

    fun removeFavorite(songId: Long) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(songId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
