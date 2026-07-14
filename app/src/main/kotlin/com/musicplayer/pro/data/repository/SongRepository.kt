package com.musicplayer.pro.data.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.room.withTransaction
import com.musicplayer.pro.data.database.MusicDatabase
import com.musicplayer.pro.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SongRepository(
    private val context: Context,
    private val database: MusicDatabase
) {
    private val contentResolver: ContentResolver = context.contentResolver
    private val songDao = database.songDao()

    fun getAllSongs(): Flow<List<Song>> = songDao.getAllSongs()

    fun searchSongs(query: String): Flow<List<Song>> {
        val searchQuery = "%$query%"
        return songDao.searchSongs(searchQuery)
    }

    suspend fun loadSongsFromMediaStore() {
        withContext(Dispatchers.IO) {
            val songs = mutableListOf<Song>()
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.YEAR
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            val cursor: Cursor? = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val dateModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
                val yearColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)

                while (it.moveToNext()) {
                    val song = Song(
                        id = it.getLong(idColumn),
                        title = it.getString(titleColumn) ?: "Unknown",
                        artist = it.getString(artistColumn) ?: "Unknown Artist",
                        album = it.getString(albumColumn) ?: "Unknown Album",
                        duration = it.getLong(durationColumn),
                        data = it.getString(dataColumn),
                        dateModified = it.getLong(dateModifiedColumn),
                        year = it.getInt(yearColumn)
                    )
                    songs.add(song)
                }
            }

            database.withTransaction {
                songDao.deleteAll()
                songDao.insertAll(songs)
            }
        }
    }
}
