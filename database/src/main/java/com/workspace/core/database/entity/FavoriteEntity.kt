package com.workspace.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val audioId: Long,
    val audioUri: String,
    val addedAt: Long = System.currentTimeMillis()
)
