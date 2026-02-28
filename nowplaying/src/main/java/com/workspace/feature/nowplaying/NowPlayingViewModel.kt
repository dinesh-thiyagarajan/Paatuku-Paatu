package com.workspace.feature.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workspace.core.database.repository.FavoriteRepository
import com.workspace.core.database.repository.ListeningHistoryRepository
import com.workspace.core.player.PlayerServiceConnection
import com.workspace.core.player.PlayerState
import com.workspace.mediaquery.data.Audio
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NowPlayingViewModel(
    private val playerServiceConnection: PlayerServiceConnection,
    private val favoriteRepository: FavoriteRepository,
    private val listeningHistoryRepository: ListeningHistoryRepository
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = playerServiceConnection.playerController
        .flatMapLatest { controller ->
            controller?.playerState ?: flowOf(PlayerState())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerState())

    val favoriteIds: StateFlow<Set<Long>> = favoriteRepository.getAllFavoriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun onPlayPause() {
        val controller = playerServiceConnection.playerController.value ?: return
        if (playerState.value.isPlaying) {
            controller.pause()
            // Record listen time for the current position
            playerState.value.currentAudio?.let { audio ->
                viewModelScope.launch {
                    listeningHistoryRepository.addListenTime(
                        audio.id,
                        playerState.value.currentPosition
                    )
                }
            }
        } else {
            controller.resume()
        }
    }

    fun onNext() {
        val controller = playerServiceConnection.playerController.value ?: return
        // Record skip if less than 80% of song played
        playerState.value.currentAudio?.let { audio ->
            val progress = if (playerState.value.duration > 0) {
                playerState.value.currentPosition.toFloat() / playerState.value.duration
            } else {
                0f
            }
            viewModelScope.launch {
                if (progress < 0.8f) {
                    listeningHistoryRepository.recordSkip(audio.id)
                } else {
                    listeningHistoryRepository.recordCompletion(audio.id)
                }
            }
        }
        controller.next()
        // Record play for the next track
        val state = playerState.value
        if (state.queue.isNotEmpty()) {
            val nextIndex = (state.currentIndex + 1) % state.queue.size
            viewModelScope.launch {
                listeningHistoryRepository.recordPlay(state.queue[nextIndex])
            }
        }
    }

    fun onPrevious() {
        playerServiceConnection.playerController.value?.previous()
    }

    fun onSeek(positionMs: Long) {
        playerServiceConnection.playerController.value?.seekTo(positionMs)
    }

    fun toggleFavorite(audio: Audio) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(audio.id)
            if (isFav) {
                favoriteRepository.removeFavorite(audio)
            } else {
                favoriteRepository.addFavorite(audio)
            }
        }
    }
}
