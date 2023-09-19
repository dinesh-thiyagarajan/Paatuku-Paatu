package com.workspace.paatukupaatu.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workspace.mediaquery.data.Audio
import com.workspace.paatukupaatu.domain.GetAudioFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(private val getAudioFilesUseCase: GetAudioFilesUseCase) :
    ViewModel() {

    private val _audios: MutableStateFlow<List<Audio>> = MutableStateFlow(listOf())
    val audios: StateFlow<List<Audio>> = _audios

    fun getAudioFiles() {
        viewModelScope.launch {
            getAudioFilesUseCase.invoke().collectLatest {
                _audios.value = it
            }
        }
    }


}