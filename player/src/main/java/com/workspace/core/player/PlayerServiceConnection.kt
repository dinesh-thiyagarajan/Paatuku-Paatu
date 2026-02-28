package com.workspace.core.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerServiceConnection(private val context: Context) {

    private val _playerController = MutableStateFlow<PlayerController?>(null)
    val playerController: StateFlow<PlayerController?> = _playerController.asStateFlow()

    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val service = (binder as MusicPlayerService.PlayerBinder).getService()
            _playerController.value = service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _playerController.value = null
        }
    }

    fun bind() {
        if (isBound) return
        val intent = Intent(context, MusicPlayerService::class.java)
        context.startService(intent)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        isBound = true
    }

    fun unbind() {
        if (!isBound) return
        try {
            context.unbindService(serviceConnection)
        } catch (_: IllegalArgumentException) {
            // Already unbound
        }
        isBound = false
    }
}
