package com.musicplayer.pro.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.musicplayer.pro.data.dao.FavoriteDao
import com.musicplayer.pro.data.dao.PlaylistDao
import com.musicplayer.pro.data.dao.SongDao
import com.musicplayer.pro.data.model.Favorite
import com.musicplayer.pro.data.model.Playlist
import com.musicplayer.pro.data.model.PlaylistSong
import com.musicplayer.pro.data.model.Song

@Database(
    entities = [Song::class, Playlist::class, PlaylistSong::class, Favorite::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var instance: MusicDatabase? = null

        fun getInstance(context: Context): MusicDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "music_player_db"
                ).build().also { instance = it }
            }
        }
    }
}
