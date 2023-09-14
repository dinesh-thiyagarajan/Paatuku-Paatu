package com.workspace.paatukupaatu

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.workspace.mediaquery.MediaQuery

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun AudioReadPermissionComposable() {
    val audioPermissionState = rememberPermissionState(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    when (audioPermissionState.status) {
        PermissionStatus.Granted -> {

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