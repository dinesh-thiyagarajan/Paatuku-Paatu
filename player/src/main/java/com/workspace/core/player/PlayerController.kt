package com.workspace.core.player

import com.workspace.mediaquery.data.Audio
import kotlinx.coroutines.flow.StateFlow

interface PlayerController {
    val playerState: StateFlow<PlayerState>

    fun play(audio: Audio, queue: List<Audio>, startIndex: Int)
    fun resume()
    fun pause()
    fun next()
    fun previous()
    fun seekTo(positionMs: Long)
    fun stop()
}
