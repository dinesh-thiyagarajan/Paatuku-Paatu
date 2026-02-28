package com.workspace.core.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.workspace.mediaquery.data.Audio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayerService : Service(), PlayerController {

    private val binder = PlayerBinder()
    private var mediaPlayer: MediaPlayer? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var positionJob: Job? = null

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    inner class PlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                if (_playerState.value.isPlaying) pause() else resume()
            }
            ACTION_NEXT -> next()
            ACTION_PREVIOUS -> previous()
            ACTION_STOP -> {
                stop()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun play(audio: Audio, queue: List<Audio>, startIndex: Int) {
        _playerState.update {
            it.copy(queue = queue, currentIndex = startIndex)
        }
        playInternal(audio)
    }

    override fun resume() {
        mediaPlayer?.let {
            it.start()
            _playerState.update { state -> state.copy(isPlaying = true) }
            startPositionTracking()
            updateNotification()
        }
    }

    override fun pause() {
        mediaPlayer?.let {
            it.pause()
            _playerState.update { state -> state.copy(isPlaying = false) }
            positionJob?.cancel()
            updateNotification()
        }
    }

    override fun next() {
        val state = _playerState.value
        if (state.queue.isEmpty()) return
        val nextIndex = (state.currentIndex + 1) % state.queue.size
        _playerState.update { it.copy(currentIndex = nextIndex) }
        playInternal(state.queue[nextIndex])
    }

    override fun previous() {
        val state = _playerState.value
        if (state.queue.isEmpty()) return
        // If more than 3 seconds in, restart current song
        if ((mediaPlayer?.currentPosition ?: 0) > 3000) {
            seekTo(0)
            return
        }
        val prevIndex = if (state.currentIndex - 1 < 0) {
            state.queue.size - 1
        } else {
            state.currentIndex - 1
        }
        _playerState.update { it.copy(currentIndex = prevIndex) }
        playInternal(state.queue[prevIndex])
    }

    override fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs.toInt())
        _playerState.update { it.copy(currentPosition = positionMs) }
    }

    override fun stop() {
        positionJob?.cancel()
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
        _playerState.update {
            PlayerState()
        }
    }

    private fun playInternal(audio: Audio) {
        positionJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(applicationContext, audio.uri)
            prepare()
            start()
            setOnCompletionListener {
                onTrackCompleted()
            }
        }

        _playerState.update {
            it.copy(
                currentAudio = audio,
                isPlaying = true,
                currentPosition = 0L,
                duration = mediaPlayer?.duration?.toLong() ?: 0L
            )
        }

        startPositionTracking()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    private fun onTrackCompleted() {
        val state = _playerState.value
        if (state.queue.isNotEmpty()) {
            next()
        } else {
            _playerState.update { it.copy(isPlaying = false) }
            positionJob?.cancel()
            updateNotification()
        }
    }

    private fun startPositionTracking() {
        positionJob?.cancel()
        positionJob = serviceScope.launch {
            while (isActive) {
                mediaPlayer?.let { player ->
                    _playerState.update {
                        it.copy(currentPosition = player.currentPosition.toLong())
                    }
                }
                delay(500L)
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Music Playback",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows currently playing music"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val audio = _playerState.value.currentAudio
        val isPlaying = _playerState.value.isPlaying

        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val contentIntent = PendingIntent.getActivity(
            this, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = PendingIntent.getService(
            this, 1,
            Intent(this, MusicPlayerService::class.java).apply {
                action = ACTION_PREVIOUS
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIntent = PendingIntent.getService(
            this, 2,
            Intent(this, MusicPlayerService::class.java).apply {
                action = ACTION_PLAY_PAUSE
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = PendingIntent.getService(
            this, 3,
            Intent(this, MusicPlayerService::class.java).apply {
                action = ACTION_NEXT
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon = if (isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }
        val playPauseTitle = if (isPlaying) "Pause" else "Play"

        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(audio?.name ?: "Paatuku Paatu")
            .setContentText(audio?.artist ?: "")
            .setSubText(audio?.album)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(contentIntent)
            .setOngoing(isPlaying)
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevIntent)
            .addAction(playPauseIcon, playPauseTitle, playPauseIntent)
            .addAction(android.R.drawable.ic_media_next, "Next", nextIntent)
            .setStyle(style)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    override fun onDestroy() {
        super.onDestroy()
        positionJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
        serviceScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "paatukupaatu_playback"
        const val NOTIFICATION_ID = 1
        const val ACTION_PLAY_PAUSE = "com.workspace.paatukupaatu.PLAY_PAUSE"
        const val ACTION_NEXT = "com.workspace.paatukupaatu.NEXT"
        const val ACTION_PREVIOUS = "com.workspace.paatukupaatu.PREVIOUS"
        const val ACTION_STOP = "com.workspace.paatukupaatu.STOP"
    }
}
