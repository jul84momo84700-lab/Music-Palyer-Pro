package com.musicplayer.pro.data.repository

import com.musicplayer.pro.data.database.MusicDatabase
import com.musicplayer.pro.data.model.Playlist
import com.musicplayer.pro.data.model.PlaylistSong
import com.musicplayer.pro.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistRepository(
    private val database: MusicDatabase
) {
    private val playlistDao = database.playlistDao()

    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()

    suspend fun createPlaylist(name: String): Long = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylist(Playlist(name = name))
    }

    suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistDao.deleteAllSongsFromPlaylist(playlist.id)
        playlistDao.deletePlaylist(playlist)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        val position = playlistDao.getPlaylistSongCount(playlistId).collect { count ->
            playlistDao.addSongToPlaylist(PlaylistSong(playlistId, songId, count))
        }
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        playlistDao.removeSongFromPlaylist(PlaylistSong(playlistId, songId))
    }

    fun getPlaylistSongs(playlistId: Long): Flow<List<Song>> = playlistDao.getPlaylistSongs(playlistId)

    fun getPlaylistSongCount(playlistId: Long): Flow<Int> = playlistDao.getPlaylistSongCount(playlistId)
}
