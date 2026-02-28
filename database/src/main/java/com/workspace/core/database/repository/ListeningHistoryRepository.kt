package com.workspace.core.database.repository

import com.workspace.core.database.dao.ListeningHistoryDao
import com.workspace.core.database.entity.ListeningHistoryEntity
import com.workspace.mediaquery.data.Audio
import kotlinx.coroutines.flow.Flow

class ListeningHistoryRepository(private val dao: ListeningHistoryDao) {

    fun getAllHistory(): Flow<List<ListeningHistoryEntity>> = dao.getAllHistory()

    fun getMostPlayed(limit: Int = 20): Flow<List<ListeningHistoryEntity>> =
        dao.getMostPlayed(limit)

    suspend fun recordPlay(audio: Audio) {
        val existing = dao.getHistoryForAudio(audio.id)
        if (existing == null) {
            dao.upsertHistory(
                ListeningHistoryEntity(
                    audioId = audio.id,
                    audioUri = audio.uri.toString(),
                    artist = audio.artist,
                    playCount = 1,
                    lastPlayedAt = System.currentTimeMillis()
                )
            )
        } else {
            dao.incrementPlayCount(audio.id, System.currentTimeMillis())
        }
    }

    suspend fun recordCompletion(audioId: Long) {
        dao.incrementCompletionCount(audioId)
    }

    suspend fun recordSkip(audioId: Long) {
        dao.incrementSkipCount(audioId)
    }

    suspend fun addListenTime(audioId: Long, durationMs: Long) {
        dao.addListenTime(audioId, durationMs)
    }

    suspend fun getHistoryForAudio(audioId: Long): ListeningHistoryEntity? =
        dao.getHistoryForAudio(audioId)
}
