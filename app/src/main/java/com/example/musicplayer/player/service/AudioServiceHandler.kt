package com.example.musicplayer.player.service

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Possible states of Audio
sealed class AudioState {
    data object Initial : AudioState()
    data class Ready(val duration: Long) : AudioState()
    data class Progress(val progress: Long) : AudioState()
    data class Buffering(val progress: Long) : AudioState()
    data class Playing(val isPlaying: Boolean) : AudioState()
    data class CurrentPlaying(val mediaItemIndex: Int) : AudioState()
}

// Possible user actions on player.
sealed class AudioPlayerEvents {
    object PlayOrPause : AudioPlayerEvents()
    object SelectedAudioChange : AudioPlayerEvents()
    object Backward : AudioPlayerEvents()
    object SeekToNext : AudioPlayerEvents()
    object Forward : AudioPlayerEvents()
    object SeekTo : AudioPlayerEvents()
    object Stop : AudioPlayerEvents()
    data class UpdateProgress(val newProgress: Float) : AudioPlayerEvents()
}

// So with this AudioServiceHandler class we can handle our audio playback.
class AudioServiceHandler @Inject constructor(
    private val exoPlayer: ExoPlayer
) : Player.Listener {
    var _audioState: MutableStateFlow<AudioState> = MutableStateFlow<AudioState>(AudioState.Initial)
        private set
    private var job: Job? = null

    // In this function we are putting one song into exo player.
    fun addMediaItem(
        mediaItem: MediaItem
    ) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    // But what if we have multiple songs which we want to play.
    fun setMultipleItems(
        mediaItems: List<MediaItem>
    ) {
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _audioState.value =
                AudioState.Buffering(exoPlayer.currentPosition)
            // So if player is ready to play then make audio file to be ready.
            ExoPlayer.STATE_READY -> _audioState.value =
                AudioState.Ready(exoPlayer.duration)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioState.value = AudioState.Playing(isPlaying = isPlaying)
        _audioState.value = AudioState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
        if (isPlaying){
            GlobalScope.launch {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    suspend fun onPlayerEvents(
        playerEvents: AudioPlayerEvents,
        selectedAudioIndex: Int = -1,
        seekPosition: Long = 0
    ) {
        when (playerEvents) {
            AudioPlayerEvents.Backward -> {
                exoPlayer.seekBack()
            }

            AudioPlayerEvents.Forward -> {
                exoPlayer.seekForward()
            }

            AudioPlayerEvents.PlayOrPause -> playOrrPause()
            AudioPlayerEvents.SeekTo -> exoPlayer.seekTo(seekPosition)
            AudioPlayerEvents.SeekToNext -> {
                exoPlayer.seekToNext()
            }

            AudioPlayerEvents.SelectedAudioChange -> {
                when (selectedAudioIndex) {
                    exoPlayer.currentMediaItemIndex -> {
                        playOrrPause()
                    }

                    else -> {
                        exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                        _audioState.value = AudioState.Playing(
                            isPlaying = true
                        )
                        exoPlayer.playWhenReady = true
                        startProgressUpdate()
                    }
                }
            }

            AudioPlayerEvents.Stop -> stopProgressUpdate()
            is AudioPlayerEvents.UpdateProgress -> {
                exoPlayer.seekTo((exoPlayer.duration * playerEvents.newProgress).toLong())
            }
        }
    }

    // An helper function regarding play or pause.
    private suspend fun playOrrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            _audioState.value = AudioState.Playing(
                isPlaying = true
            )
            startProgressUpdate()
        }
    }

    // So after .5 seconds it give us progress update.
    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500)
            _audioState.value = AudioState.Progress(exoPlayer.currentPosition)
        }
    }

    // If user clicks on pause, then stop the progress update.
    private fun stopProgressUpdate() {
        job?.cancel()
        _audioState.value = AudioState.Playing(isPlaying = false)
    }
}