package com.example.musicplayer.ui.audio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musicplayer.data.local.model.Audio

@Composable
fun HomeScreen(
    progress: Float,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    audioList: List<Audio>,
    onStart: (Int) -> Unit,
    onItemClick: (Int) -> Unit,
    onNext: () -> Unit
) {
    Scaffold(
        bottomBar = {}
    ) { innerPadding ->

    }
}

/**
 * Bottom bar which represents our player.
 */
@Composable
fun AudioPlayer(
    progress: Float,
    audio: Audio,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onProgress: (Float) -> Unit
) {
    BottomAppBar {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ArtistInfo(audio = audio, modifier = Modifier.weight(1f))
                MediaPlayerController(
                    isAudioPlaying = isAudioPlaying,
                    onStart,
                    onNext
                )
                Slider(value = progress, onValueChange = { onProgress(it) }, valueRange = 0f..100f)
            }
        }
    }
}

/**
 * Composable function to create a media controller, through which we control how we want to play , pause.
 */
@Composable
fun MediaPlayerController(
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerIcon(
            icon = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
        ) {
            onStart
        }
        Spacer(modifier = Modifier.size(8.dp))
        IconButton(
            onClick = { onNext }
        ) {
            Icon(imageVector = Icons.Default.SkipNext, contentDescription = null)
        }
    }
}

/**
 * A Composable for artist info.
 */
@Composable
fun ArtistInfo(
    modifier: Modifier = Modifier,
    audio: Audio
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerIcon(
            icon = Icons.Default.MusicNote,
            borderStroke = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        ) {}
        Spacer(modifier = Modifier.size(4.dp))
        Column {
            Text(
                modifier = Modifier.weight(1f),
                text = audio.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = audio.artist,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
    }
}

/**
 * A composable for player icon
 */
@Composable
fun PlayerIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    borderStroke: BorderStroke? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        border = borderStroke,
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick
            },
        contentColor = color,
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}