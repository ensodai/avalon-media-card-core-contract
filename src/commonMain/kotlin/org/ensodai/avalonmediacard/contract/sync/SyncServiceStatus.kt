package org.ensodai.avalonmediacard.contract.sync

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SyncServiceStatus(
    val service: String,
    val status: SyncStatus,
    val lastSyncedAt: Instant?,
    val errorMessage: String? = null
)
