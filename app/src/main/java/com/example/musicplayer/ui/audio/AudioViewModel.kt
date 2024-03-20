package com.example.musicplayer.ui.audio

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.musicplayer.data.local.model.Audio
import com.example.musicplayer.data.repository.AudioRepository
import com.example.musicplayer.player.service.AudioPlayerEvents
import com.example.musicplayer.player.service.AudioServiceHandler
import com.example.musicplayer.player.service.AudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// Let's create dummy audio object.
private val dummyAudioObject = Audio(
    uri = "".toUri(),
    displayName = "",
    id = 0L,
    data = "",
    duration = 0,
    title = "",
    artist = ""
)

// States of UI.
sealed class UIState{
    data object Initial: UIState()
    data object Ready: UIState()
}
// Possible user actions on UI.
sealed class UIEvents{
    data object PlayOrPause: UIEvents()
    data class SelectedAudioChange(val index: Int): UIEvents()
    data class SeekTo(val position: Float): UIEvents()
    data object SeekToNext: UIEvents()
    data object Forward: UIEvents()
    data object Backward: UIEvents()
    data class UpdateProgress(val newProgress: Float): UIEvents()
}

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioServiceHandler: AudioServiceHandler,
    private val audioRepository: AudioRepository,
    savedStateHandle: SavedStateHandle // This helps us to get data from UI pass it to viewmodel.
): ViewModel() {

    // So below we have used saveable function of saveStateHandle.
    var duration by savedStateHandle.saveable {
        mutableLongStateOf(0L)
    }
    var progress by savedStateHandle.saveable {
        mutableFloatStateOf(0f)
    }
    var progressString by savedStateHandle.saveable {
        mutableStateOf("00:00")
    }
    var isPlaying by savedStateHandle.saveable {
        mutableStateOf(false)
    }
    var currentSelectedAudio by savedStateHandle.saveable {
        mutableStateOf(dummyAudioObject)
    }
    var audioList by savedStateHandle.saveable {
        mutableStateOf(listOf<Audio>())
    }

    var _uiState = MutableStateFlow<UIState>(UIState.Initial)
        private set

    init {
        loadAudioData()

        viewModelScope.launch {
            audioServiceHandler._audioState.collectLatest { state ->
                when(state){
                    // if audio state is in buffering state
                    is AudioState.Buffering -> calculateProgressValue(state.progress)
                    is AudioState.CurrentPlaying -> {
                        currentSelectedAudio = audioList[state.mediaItemIndex]
                    }
                    AudioState.Initial -> _uiState.value = UIState.Initial

                    // whether audio is playing or not and depending upon that update this state
                    is AudioState.Playing -> isPlaying = state.isPlaying
                    is AudioState.Progress -> calculateProgressValue(state.progress)
                    is AudioState.Ready -> {
                        duration = state.duration
                        _uiState.value = UIState.Ready
                    }
                }
            }
        }
    }

    // Get data from repository
    private fun loadAudioData(){
        viewModelScope.launch {
            val audio = audioRepository.getAudioData()
            audioList = audio
            setMediaItems()
        }
    }

    // Then create media items and set it to exo player using audio service handler.
    private fun setMediaItems(){
        audioList.map { audio ->
            MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audio.artist)
                        .setDisplayTitle(audio.title)
                        .setSubtitle(audio.displayName)
                        .build()
                )
                .build()
        }.also { audioServiceHandler.setMultipleItems(it) }
    }

    // Progress in % format.
    private fun calculateProgressValue(currentProgress: Long){
        progress = if (currentProgress > 0L) ((currentProgress.toFloat() / duration.toFloat()) * 100f) else 0f
        progressString = formatDuration(currentProgress)
    }

    // Function which formats the duration.
    fun formatDuration(duration: Long): String{
        val minute = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (minute) - (minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        // after obtaining minute and seconds, format them in this way.
        return String.format("%02d:%02d", minute, seconds)
    }

    // A function which handles UI Events.
    fun onUIEvents(
        uiEvents: UIEvents
    ){
        viewModelScope.launch {
            when(uiEvents){
                UIEvents.Backward -> {
                    audioServiceHandler.onPlayerEvents(AudioPlayerEvents.Backward)
                }
                UIEvents.Forward -> audioServiceHandler.onPlayerEvents(AudioPlayerEvents.Forward)
                UIEvents.PlayOrPause -> {
                    audioServiceHandler.onPlayerEvents(AudioPlayerEvents.PlayOrPause)
                }
                is UIEvents.SeekTo -> {
                    audioServiceHandler.onPlayerEvents(AudioPlayerEvents.SeekTo, seekPosition = ())
                }
                UIEvents.SeekToNext -> audioServiceHandler.onPlayerEvents(AudioPlayerEvents.SeekToNext)
                is UIEvents.SelectedAudioChange -> {
                    audioServiceHandler.onPlayerEvents(
                        AudioPlayerEvents.SelectedAudioChange,
                        selectedAudioIndex = uiEvents.index
                    )
                }
                is UIEvents.UpdateProgress -> {
                    audioServiceHandler.onPlayerEvents(
                        AudioPlayerEvents.UpdateProgress(
                            newProgress = uiEvents.newProgress
                        )
                    )
                }
            }
        }
    }
}
