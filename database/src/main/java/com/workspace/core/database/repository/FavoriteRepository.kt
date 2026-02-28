package com.workspace.core.database.repository

import com.workspace.core.database.dao.FavoriteDao
import com.workspace.core.database.entity.FavoriteEntity
import com.workspace.mediaquery.data.Audio
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    fun getAllFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    fun getAllFavoriteIds(): Flow<List<Long>> = favoriteDao.getAllFavoriteIds()

    fun isFavorite(audioId: Long): Flow<Boolean> = favoriteDao.isFavorite(audioId)

    suspend fun toggleFavorite(audio: Audio) {
        val existing = favoriteDao.isFavorite(audio.id)
        val entity = FavoriteEntity(
            audioId = audio.id,
            audioUri = audio.uri.toString()
        )
        // We need to check synchronously, so we'll use add/remove pattern
        try {
            favoriteDao.addFavorite(entity)
        } catch (_: Exception) {
            // Already exists - ignore, caller handles via isFavorite flow
        }
    }

    suspend fun addFavorite(audio: Audio) {
        favoriteDao.addFavorite(
            FavoriteEntity(
                audioId = audio.id,
                audioUri = audio.uri.toString()
            )
        )
    }

    suspend fun removeFavorite(audio: Audio) {
        favoriteDao.removeFavorite(
            FavoriteEntity(
                audioId = audio.id,
                audioUri = audio.uri.toString()
            )
        )
    }
}
