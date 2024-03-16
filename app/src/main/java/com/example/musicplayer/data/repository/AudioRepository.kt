package com.example.musicplayer.data.repository

import com.example.musicplayer.data.local.ContentResolverHelper
import com.example.musicplayer.data.local.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val contentResolverHelper: ContentResolverHelper    // Now if we are fetching data from networking request, then we can replace it here with that data.
) {
    suspend fun getAudioData(): List<Audio> = withContext(Dispatchers.IO){
        contentResolverHelper.getAudioData()
    }
}