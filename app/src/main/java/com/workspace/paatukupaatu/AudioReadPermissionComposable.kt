package com.workspace.paatukupaatu

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.workspace.mediaquery.MediaQuery
import com.workspace.paatukupaatu.MusicPlayerService.Companion.URI_KEY
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun AudioReadPermissionComposable() {
    val coroutineScope = rememberCoroutineScope()
    val audioPermissionState = rememberPermissionState(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    val localContext = LocalContext.current

    when (audioPermissionState.status) {
        PermissionStatus.Granted -> {
            SideEffect {
                coroutineScope.launch {
                    MediaQuery(localContext.contentResolver).queryAudio().collectLatest {
                        Log.e("Result", it.toString())
                        val intent = Intent(localContext, MusicPlayerService::class.java)
                        intent.putExtra(URI_KEY, it[0].uri.toString())
                        localContext.startService(intent)
                    }
                }
            }
        }

        is PermissionStatus.Denied -> {
            Column {
                val launcher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {}
                SideEffect {
                    launcher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }
        }
    }
}

@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}