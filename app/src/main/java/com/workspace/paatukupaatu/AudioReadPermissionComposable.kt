package com.workspace.paatukupaatu

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.workspace.paatukupaatu.ui.viewModel.AudioViewModel
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun AudioReadPermissionComposable() {

    val audioViewModel: AudioViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val audioPermissionState = rememberPermissionState(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    val audios = audioViewModel.audios.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(audios.value) {
            Text(text = it.name)
        }
    }

    when (audioPermissionState.status) {
        PermissionStatus.Granted -> {
            SideEffect {
                coroutineScope.launch {
                    audioViewModel.getAudioFiles()
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