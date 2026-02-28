package com.workspace.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workspace.core.database.repository.FavoriteRepository
import com.workspace.core.database.repository.ListeningHistoryRepository
import com.workspace.core.player.PlayerServiceConnection
import com.workspace.core.player.PlayerState
import com.workspace.feature.home.ml.RecommendationEngine
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAudioFilesUseCase: GetAudioFilesUseCase,
    private val playerServiceConnection: PlayerServiceConnection,
    private val favoriteRepository: FavoriteRepository,
    private val listeningHistoryRepository: ListeningHistoryRepository
) : ViewModel() {

    private val _allAudios = MutableStateFlow<List<Audio>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _recommendations = MutableStateFlow<List<Audio>>(emptyList())
    val recommendations: StateFlow<List<Audio>> = _recommendations.asStateFlow()

    private val recommendationEngine = RecommendationEngine()

    val audios: StateFlow<List<Audio>> = combine(
        _allAudios,
        _searchQuery
    ) { audios, query ->
        if (query.isBlank()) {
            audios
        } else {
            audios.filter { audio ->
                audio.name.contains(query, ignoreCase = true) ||
                    audio.artist.contains(query, ignoreCase = true) ||
                    audio.album.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteIds: StateFlow<Set<Long>> = favoriteRepository.getAllFavoriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val playerState: StateFlow<PlayerState> = playerServiceConnection.playerController
        .flatMapLatest { controller ->
            controller?.playerState ?: flowOf(PlayerState())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerState())

    fun loadAudios() {
        viewModelScope.launch {
            _isLoading.value = true
            getAudioFilesUseCase().collectLatest { audioList ->
                _allAudios.value = audioList
                _isLoading.value = false
                updateRecommendations()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onAudioSelected(audio: Audio) {
        val queue = _allAudios.value
        val index = queue.indexOf(audio).coerceAtLeast(0)
        playerServiceConnection.playerController.value?.play(audio, queue, index)
        viewModelScope.launch {
            listeningHistoryRepository.recordPlay(audio)
        }
    }

    fun toggleFavorite(audio: Audio) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(audio.id)
            if (isFav) {
                favoriteRepository.removeFavorite(audio)
            } else {
                favoriteRepository.addFavorite(audio)
            }
            updateRecommendations()
        }
    }

    fun onPlayPause() {
        val controller = playerServiceConnection.playerController.value ?: return
        if (playerState.value.isPlaying) {
            controller.pause()
        } else {
            controller.resume()
        }
    }

    private fun updateRecommendations() {
        viewModelScope.launch {
            listeningHistoryRepository.getAllHistory().collectLatest { history ->
                _recommendations.value = recommendationEngine.recommend(
                    allAudios = _allAudios.value,
                    history = history,
                    favoriteIds = favoriteIds.value
                )
            }
        }
    }
}
