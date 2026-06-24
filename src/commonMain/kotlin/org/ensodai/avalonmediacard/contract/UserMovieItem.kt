package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class UserMovieItem(
    val id: Uuid,
    val userId: Uuid,
    val catalogId: String,
    val mediaId: String,
    val mediaType: MediaType,
    val status: MediaStatus,
    val userRating: Int? = null,
    val progressSeconds: Long = 0,
    val durationSeconds: Long = 0,
    val lastWatchedAt: Instant
)
