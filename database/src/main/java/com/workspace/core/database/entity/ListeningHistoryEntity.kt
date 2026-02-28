package com.workspace.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listening_history")
data class ListeningHistoryEntity(
    @PrimaryKey val audioId: Long,
    val audioUri: String,
    val artist: String,
    val playCount: Int = 0,
    val totalListenTimeMs: Long = 0,
    val lastPlayedAt: Long = 0,
    val completionCount: Int = 0,
    val skipCount: Int = 0
)
