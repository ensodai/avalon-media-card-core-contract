package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class UserEpisodeItem(
    val id: Uuid,
    val userId: Uuid,
    val catalogId: String,
    val mediaId: String,
    val season: Int,
    val episode: Int,
    val progressSeconds: Long = 0,
    val durationSeconds: Long = 0,
    val isWatched: Boolean = false,
    val inCollection: Boolean = false,
    val lastWatchedAt: Instant
)
