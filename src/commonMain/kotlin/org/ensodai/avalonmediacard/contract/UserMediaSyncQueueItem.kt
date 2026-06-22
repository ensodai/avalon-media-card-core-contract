package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class UserMediaSyncQueueItem(
    val id: String,
    val userId: String,
    val mediaType: MediaType,
    val mediaId: String,
    val service: String, // "trakt", "myshows"
    val action: SyncAction,
    val progressSeconds: Long? = null,
    val durationSeconds: Long? = null,
    val rating: Int? = null,
    val status: SyncStatus,
    val attempts: Int = 0,
    val lastAttemptAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)
