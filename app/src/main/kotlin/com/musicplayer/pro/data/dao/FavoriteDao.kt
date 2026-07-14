package com.musicplayer.pro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.musicplayer.pro.data.model.Favorite
import com.musicplayer.pro.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    @Delete
    suspend fun removeFavorite(favorite: Favorite)

    @Query("SELECT songs.* FROM songs INNER JOIN favorites ON songs.id = favorites.songId ORDER BY favorites.addedAt DESC")
    fun getFavoriteSongs(): Flow<List<Song>>

    @Query("SELECT COUNT(*) > 0 FROM favorites WHERE songId = :songId")
    fun isFavorite(songId: Long): Flow<Boolean>

    @Query("SELECT songId FROM favorites")
    fun getFavoriteSongIds(): Flow<List<Long>>
}
