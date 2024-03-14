package com.example.musicplayer.data.local.model

import android.net.Uri

/**
 * This audio class holds the data which we want to play.
 */
data class Audio(
    val uri: Uri,
    val displayName: String,
    val id: Long,
    val data: String,
    val duration: Int,
    val title: String
)
