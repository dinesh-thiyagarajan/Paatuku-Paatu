package com.workspace.paatukupaatu.domain

import com.workspace.mediaquery.MediaQuery

class GetAudioFilesUseCase(private val mediaQuery: MediaQuery) {
    suspend operator fun invoke() = mediaQuery.queryAudio()
}