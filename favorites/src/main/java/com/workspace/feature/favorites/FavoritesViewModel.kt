package com.workspace.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workspace.core.database.repository.FavoriteRepository
import com.workspace.core.player.PlayerServiceConnection
import com.workspace.core.player.PlayerState
import com.workspace.mediaquery.data.Audio
import com.workspace.mediaquery.domain.GetAudioFilesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val getAudioFilesUseCase: GetAudioFilesUseCase,
    private val playerServiceConnection: PlayerServiceConnection
) : ViewModel() {

    private val _allAudios = MutableStateFlow<List<Audio>>(emptyList())

    val favoriteSongs: StateFlow<List<Audio>> = combine(
        _allAudios,
        favoriteRepository.getAllFavoriteIds()
    ) { allAudios, favoriteIds ->
        allAudios.filter { favoriteIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playerState: StateFlow<PlayerState> = playerServiceConnection.playerController
        .flatMapLatest { controller ->
            controller?.playerState ?: flowOf(PlayerState())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerState())

    fun loadAudios() {
        viewModelScope.launch {
            getAudioFilesUseCase().collectLatest { audioList ->
                _allAudios.value = audioList
            }
        }
    }

    fun onAudioSelected(audio: Audio) {
        val favorites = favoriteSongs.value
        val index = favorites.indexOf(audio).coerceAtLeast(0)
        playerServiceConnection.playerController.value?.play(audio, favorites, index)
    }

    fun removeFavorite(audio: Audio) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(audio)
        }
    }
}
