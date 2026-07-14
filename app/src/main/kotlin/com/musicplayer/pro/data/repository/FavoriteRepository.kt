package com.musicplayer.pro.data.repository

import com.musicplayer.pro.data.database.MusicDatabase
import com.musicplayer.pro.data.model.Favorite
import com.musicplayer.pro.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoriteRepository(
    private val database: MusicDatabase
) {
    private val favoriteDao = database.favoriteDao()

    fun getFavoriteSongs(): Flow<List<Song>> = favoriteDao.getFavoriteSongs()

    fun isFavorite(songId: Long): Flow<Boolean> = favoriteDao.isFavorite(songId)

    suspend fun addFavorite(songId: Long) = withContext(Dispatchers.IO) {
        favoriteDao.addFavorite(Favorite(songId))
    }

    suspend fun removeFavorite(songId: Long) = withContext(Dispatchers.IO) {
        favoriteDao.removeFavorite(Favorite(songId))
    }

    fun getFavoriteSongIds(): Flow<List<Long>> = favoriteDao.getFavoriteSongIds()
}
