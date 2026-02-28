package com.workspace.feature.home.ml

import com.workspace.core.database.entity.ListeningHistoryEntity
import com.workspace.mediaquery.data.Audio

class RecommendationEngine {

    fun recommend(
        allAudios: List<Audio>,
        history: List<ListeningHistoryEntity>,
        favoriteIds: Set<Long>,
        limit: Int = 10
    ): List<Audio> {
        if (history.isEmpty()) {
            // Cold start: return random selection
            return allAudios.shuffled().take(limit)
        }

        val historyMap = history.associateBy { it.audioId }

        // Calculate artist affinity scores
        val artistScores = mutableMapOf<String, Double>()
        for (entry in history) {
            val artist = entry.artist
            val completionRate = if (entry.playCount > 0) {
                entry.completionCount.toDouble() / entry.playCount
            } else {
                0.0
            }
            val score = (entry.playCount * 3.0) +
                (completionRate * 5.0) +
                (if (favoriteIds.contains(entry.audioId)) 10.0 else 0.0) -
                (if (entry.playCount > 0) {
                    (entry.skipCount.toDouble() / entry.playCount) * 2.0
                } else {
                    0.0
                })
            artistScores[artist] = (artistScores[artist] ?: 0.0) + score
        }

        // Score each audio
        val scoredAudios = allAudios.map { audio ->
            val personalScore = historyMap[audio.id]?.let { h ->
                val completionRate = if (h.playCount > 0) {
                    h.completionCount.toDouble() / h.playCount
                } else {
                    0.0
                }
                (h.playCount * 2.0) + (completionRate * 5.0) -
                    (if (h.playCount > 0) {
                        (h.skipCount.toDouble() / h.playCount) * 3.0
                    } else {
                        0.0
                    })
            } ?: 0.0

            val artistAffinity = artistScores[audio.artist] ?: 0.0
            val favoriteBonus = if (favoriteIds.contains(audio.id)) 8.0 else 0.0

            // Novelty bonus: songs with fewer plays get a boost
            val playCount = historyMap[audio.id]?.playCount ?: 0
            val noveltyBonus = if (playCount == 0) 5.0 else 1.0 / playCount

            val totalScore = personalScore + (artistAffinity * 0.5) +
                favoriteBonus + noveltyBonus

            audio to totalScore
        }

        return scoredAudios
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }
}
