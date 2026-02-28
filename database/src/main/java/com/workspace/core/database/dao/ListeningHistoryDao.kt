package com.workspace.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.workspace.core.database.entity.ListeningHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListeningHistoryDao {

    @Query("SELECT * FROM listening_history ORDER BY lastPlayedAt DESC")
    fun getAllHistory(): Flow<List<ListeningHistoryEntity>>

    @Query("SELECT * FROM listening_history WHERE audioId = :audioId")
    suspend fun getHistoryForAudio(audioId: Long): ListeningHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHistory(history: ListeningHistoryEntity)

    @Query(
        "UPDATE listening_history SET playCount = playCount + 1, " +
            "lastPlayedAt = :timestamp WHERE audioId = :audioId"
    )
    suspend fun incrementPlayCount(audioId: Long, timestamp: Long)

    @Query(
        "UPDATE listening_history SET completionCount = completionCount + 1 " +
            "WHERE audioId = :audioId"
    )
    suspend fun incrementCompletionCount(audioId: Long)

    @Query(
        "UPDATE listening_history SET skipCount = skipCount + 1 " +
            "WHERE audioId = :audioId"
    )
    suspend fun incrementSkipCount(audioId: Long)

    @Query(
        "UPDATE listening_history SET totalListenTimeMs = totalListenTimeMs + :durationMs " +
            "WHERE audioId = :audioId"
    )
    suspend fun addListenTime(audioId: Long, durationMs: Long)

    @Query("SELECT * FROM listening_history ORDER BY playCount DESC LIMIT :limit")
    fun getMostPlayed(limit: Int = 20): Flow<List<ListeningHistoryEntity>>
}
