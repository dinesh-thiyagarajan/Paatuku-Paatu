package com.workspace.core.player

import com.workspace.mediaquery.data.Audio

data class PlayerState(
    val currentAudio: Audio? = null,
    val queue: List<Audio> = emptyList(),
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L
)
