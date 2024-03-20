package com.example.musicplayer.ui.audio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

            }
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
            }
    ) {

    }
}