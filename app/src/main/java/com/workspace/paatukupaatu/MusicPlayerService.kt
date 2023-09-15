package com.workspace.paatukupaatu


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat


class MusicPlayerService : Service() {

    private val mediaPlayer = MediaPlayer()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val receivedUri = intent?.extras.let {
            it?.getString(URI_KEY)
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

        val notification = NotificationCompat.Builder(this, "101")
            .setContentTitle("Song Song")
            .setContentText("Paatu")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        receivedUri?.let {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(this.applicationContext, Uri.parse(it))
            mediaPlayer.prepare()
            mediaPlayer.start()
        }

        mediaPlayer.setOnCompletionListener {
            Log.e("State", it.isPlaying.toString())
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun stopService(name: Intent?): Boolean {
        mediaPlayer.release()
        mediaPlayer.stop()
        return super.stopService(name)
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_NAME = "paatukupaatu"
        private const val NOTIFICATION_CHANNEL_ID = "101"
        const val URI_KEY = "uri"
    }
}