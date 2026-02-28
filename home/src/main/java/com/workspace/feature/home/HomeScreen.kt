package com.workspace.feature.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.workspace.feature.home.components.RecommendationSection
import com.workspace.feature.home.components.SearchBar
import com.workspace.feature.home.components.SongListItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavigateToNowPlaying: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val audioPermissionState = rememberPermissionState(
        Manifest.permission.READ_MEDIA_AUDIO
    )

    val audios by viewModel.audios.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val playerState by viewModel.playerState.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    when (audioPermissionState.status) {
        PermissionStatus.Granted -> {
            LaunchedEffect(Unit) {
                viewModel.loadAudios()
            }

            Column(modifier = modifier.fillMaxSize()) {
                SearchBar(
                    query = searchQuery,
                    onQueryChanged = viewModel::onSearchQueryChanged
                )

                if (isLoading && audios.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (audios.isEmpty() && searchQuery.isBlank()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.MusicOff,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Text(
                                text = "No music files found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add audio files to your device to get started",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (searchQuery.isBlank() && recommendations.isNotEmpty()) {
                            item {
                                RecommendationSection(
                                    recommendations = recommendations,
                                    onAudioClick = { audio ->
                                        viewModel.onAudioSelected(audio)
                                        onNavigateToNowPlaying()
                                    }
                                )
                            }
                        }

                        if (audios.isNotEmpty()) {
                            item {
                                Text(
                                    text = if (searchQuery.isBlank()) "All Songs"
                                    else "Results (${audios.size})",
                                    style = androidx.compose.material3.MaterialTheme
                                        .typography.titleMedium,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                )
                            }
                        }

                        items(audios, key = { it.id }) { audio ->
                            SongListItem(
                                audio = audio,
                                isFavorite = favoriteIds.contains(audio.id),
                                isPlaying = playerState.currentAudio?.id == audio.id,
                                onClick = {
                                    viewModel.onAudioSelected(audio)
                                    onNavigateToNowPlaying()
                                },
                                onFavoriteToggle = {
                                    viewModel.toggleFavorite(audio)
                                }
                            )
                            Divider(
                                modifier = Modifier.padding(start = 76.dp)
                            )
                        }

                        if (audios.isEmpty() && searchQuery.isNotBlank()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No songs found for \"$searchQuery\"")
                                }
                            }
                        }
                    }
                }
            }
        }

        is PermissionStatus.Denied -> {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) {}
            SideEffect {
                launcher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Audio permission is required to show your music.")
            }
        }
    }
}
