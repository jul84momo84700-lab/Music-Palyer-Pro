package com.musicplayer.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val data: String,
    val albumArt: String? = null,
    val dateModified: Long,
    val year: Int = 0,
    val genre: String? = null
)
