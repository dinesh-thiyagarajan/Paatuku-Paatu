package com.workspace.paatukupaatu.ui.viewModel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workspace.mediaquery.data.Audio
import com.workspace.paatukupaatu.MusicPlayerService
import com.workspace.paatukupaatu.MusicPlayerService.Companion.AUDIO_DATA
import com.workspace.paatukupaatu.domain.GetAudioFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val getAudioFilesUseCase: GetAudioFilesUseCase,
    @ApplicationContext private val applicationContext: Context
) :
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

    fun audioSelected(audio: Audio) {
        val intent = Intent(applicationContext, MusicPlayerService::class.java)
        val bundle = Bundle()
        bundle.putParcelable(AUDIO_DATA, audio)
        intent.putExtras(bundle)
        startMusicPlayerService(intent)
    }

    private fun startMusicPlayerService(intent: Intent) =
        applicationContext.startService(intent)

}