package com.workspace.paatukupaatu


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.workspace.mediaquery.data.Audio


class MusicPlayerService : Service() {

    private val mediaPlayer = MediaPlayer()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val audio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(AUDIO_DATA, Audio::class.java)
        } else {
            intent?.getParcelableExtra(AUDIO_DATA)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val manager = getSystemService(
            NotificationManager::class.java
        )
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.description = "This is channel 2"
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(audio?.name)
            .setContentText(audio?.mimeType)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        audio?.let {
            val audioAttributes = AudioAttributes.Builder().build()
            mediaPlayer.reset()
            mediaPlayer.setAudioAttributes(audioAttributes)
            mediaPlayer.setDataSource(this.applicationContext, it.uri)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun stopService(name: Intent?): Boolean {
        mediaPlayer.stop()
        mediaPlayer.release()
        return super.stopService(name)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_NAME = "paatukupaatu"
        private const val NOTIFICATION_CHANNEL_ID = "101"
        const val AUDIO_DATA = "audio_data"
    }
}