package com.musicplayer.pro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.musicplayer.pro.data.database.MusicDatabase
import com.musicplayer.pro.data.model.Song
import com.musicplayer.pro.data.repository.FavoriteRepository
import com.musicplayer.pro.service.MediaPlaybackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val isShuffle: Boolean = false,
    val isFavorite: Boolean = false,
    val playlistSongs: List<Song> = emptyList(),
    val currentIndex: Int = 0
)

class PlayerViewModel(private val context: Context) : ViewModel() {
    private val database = MusicDatabase.getInstance(context)
    private val favoriteRepository = FavoriteRepository(database)
    private val mediaManager = MediaPlaybackManager.getInstance(context)

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        setupMediaListener()
    }

    private fun setupMediaListener() {
        viewModelScope.launch {
            mediaManager.currentSong.collect { song ->
                _uiState.update { it.copy(currentSong = song) }
                if (song != null) {
                    favoriteRepository.isFavorite(song.id).collect { isFav ->
                        _uiState.update { it.copy(isFavorite = isFav) }
                    }
                }
            }
        }

        viewModelScope.launch {
            mediaManager.isPlaying.collect { isPlaying ->
                _uiState.update { it.copy(isPlaying = isPlaying) }
            }
        }

        viewModelScope.launch {
            mediaManager.currentPosition.collect { position ->
                _uiState.update { it.copy(currentPosition = position) }
            }
        }

        viewModelScope.launch {
            mediaManager.duration.collect { duration ->
                _uiState.update { it.copy(duration = duration) }
            }
        }

        viewModelScope.launch {
            mediaManager.repeatMode.collect { mode ->
                _uiState.update { it.copy(repeatMode = mode) }
            }
        }

        viewModelScope.launch {
            mediaManager.isShuffle.collect { shuffle ->
                _uiState.update { it.copy(isShuffle = shuffle) }
            }
        }
    }

    fun playSong(song: Song) {
        mediaManager.playSong(song)
    }

    fun togglePlayPause() {
        if (_uiState.value.isPlaying) {
            mediaManager.pause()
        } else {
            mediaManager.play()
        }
    }

    fun nextSong() {
        mediaManager.next()
    }

    fun previousSong() {
        mediaManager.previous()
    }

    fun seekTo(position: Long) {
        mediaManager.seekTo(position)
    }

    fun toggleRepeatMode() {
        mediaManager.toggleRepeatMode()
    }

    fun toggleShuffle() {
        mediaManager.toggleShuffle()
    }

    fun toggleFavorite() {
        val song = _uiState.value.currentSong ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoriteRepository.removeFavorite(song.id)
            } else {
                favoriteRepository.addFavorite(song.id)
            }
        }
    }
}
