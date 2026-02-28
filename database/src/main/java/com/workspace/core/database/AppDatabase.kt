package com.workspace.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.workspace.core.database.dao.FavoriteDao
import com.workspace.core.database.dao.ListeningHistoryDao
import com.workspace.core.database.entity.FavoriteEntity
import com.workspace.core.database.entity.ListeningHistoryEntity

@Database(
    entities = [FavoriteEntity::class, ListeningHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun listeningHistoryDao(): ListeningHistoryDao
}
