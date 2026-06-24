package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class UserMediaSyncQueueItem(
    val id: Uuid,
    val userId: Uuid,
    val mediaType: MediaType,
    val mediaId: String,
    val service: String, // "trakt", "myshows"
    val action: SyncAction,
    val progressSeconds: Long? = null,
    val durationSeconds: Long? = null,
    val rating: Int? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val status: SyncStatus,
    val attempts: Int = 0,
    val lastAttemptAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
