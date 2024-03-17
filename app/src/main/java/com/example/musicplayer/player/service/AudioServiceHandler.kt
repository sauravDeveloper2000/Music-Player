package com.example.musicplayer.player.service

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject

// Possible states of Audio
sealed class AudioState{
    data object Initial: AudioState()
    data class Ready(val duration: Long): AudioState()
    data class Progress(val progress: Long): AudioState()
    data class Buffering(val progress: Long): AudioState()
    data class Playing(val isPlaying: Boolean): AudioState()
    data class CurrentPlaying(val mediaItemIndex: Int): AudioState()
}

// Possible user actions on player.
sealed class AudioPlayerEvents{
    object PlayOrPause: AudioPlayerEvents()
    object SelectedAudioChange: AudioPlayerEvents()
    object Backward: AudioPlayerEvents()
    object SeekToNext: AudioPlayerEvents()
    object Forward: AudioPlayerEvents()
    object SeekTo: AudioPlayerEvents()
    object Stop: AudioPlayerEvents()
    data class UpdateProgress(val newProgress: Float): AudioPlayerEvents()
}

// So with this AudioServiceHandler class we can handle our audio playback.
class AudioServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer
): Player.Listener {
}