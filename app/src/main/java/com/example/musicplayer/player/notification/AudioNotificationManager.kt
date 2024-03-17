package com.example.musicplayer.player.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.musicplayer.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val NOTIFICATION_ID = 101
private const val NOTIFICATION_CHANNEL_NAME = "notification channel 1"
private const val NOTIFICATION_CHANNEL_ID = "notification channel id 1"

class AudioNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer
) {
    // So to create notificationManager we utilize the Notification Manager Compat.
    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    init {
        notificationChannel()
    }

    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ){
        buildNotification(mediaSession)
        startForegroundNotificationService(mediaSessionService)
    }

    private fun startForegroundNotificationService(
        mediaSessionService: MediaSessionService
    ){
        val notification = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    // an helper function which advertise our media session to notification.
    // also an helper function which builds our notification.
    @OptIn(UnstableApi::class) private fun buildNotification(
        mediaSession: MediaSession
    ){
        PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_NAME
        )
            .setMediaDescriptionAdapter(
                AudioNotificationAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity // session activity provide us pending intent.
                )
            )
            .setSmallIconResourceId(R.drawable.ic_playing_music)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)
                it.setUseFastForwardActionInCompactView(true)
                it.setUseRewindActionInCompactView(true)
                it.setUseNextActionInCompactView(true)
                it.setPriority(NotificationCompat.PRIORITY_LOW)
                it.setPlayer(exoPlayer)
            }
    }

    /**
     * So before creating notification, create notification channel.
     */
    private fun notificationChannel(){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}