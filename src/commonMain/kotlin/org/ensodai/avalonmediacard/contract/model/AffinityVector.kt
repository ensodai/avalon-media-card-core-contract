package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable

@Serializable
data class AffinityVector(
    val genreWeights: Map<String, Double> = emptyMap(),
    val keywordWeights: Map<String, Double> = emptyMap(),
    val directorWeights: Map<String, Double> = emptyMap(),
    val actorWeights: Map<String, Double> = emptyMap(),
    val companyWeights: Map<String, Double> = emptyMap(),
    val pacingWeights: Map<String, Double> = emptyMap(),
    val eraWeights: Map<String, Double> = emptyMap(),
    val moodWeights: Map<String, Double> = emptyMap(),
    
    // --- Кратковременная память (Сессионный контекст) ---
    val recentWatchedIds: List<String> = emptyList(), // Хронология недавних просмотров
    val sessionBingeVector: Map<String, Double> = emptyMap() // "Запойный" вектор текущей сессии
)
