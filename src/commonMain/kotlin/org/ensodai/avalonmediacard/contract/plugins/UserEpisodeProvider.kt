package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UserEpisodeProgress(
    val season: Int,
    val episode: Int,
    val progressSeconds: Long,
    val durationSeconds: Long,
    val isWatched: Boolean,
    val userRating: Int? = null
)

interface UserEpisodeProvider {
    suspend fun getEpisodesProgress(userId: Uuid, mediaId: String, catalogId: String = "tmdb"): List<UserEpisodeProgress>
    suspend fun saveEpisodeProgress(
        userId: Uuid,
        catalogId: String = "tmdb",
        mediaId: String,
        season: Int,
        episode: Int,
        progressSeconds: Long,
        durationSeconds: Long,
        isWatched: Boolean
    )
}
