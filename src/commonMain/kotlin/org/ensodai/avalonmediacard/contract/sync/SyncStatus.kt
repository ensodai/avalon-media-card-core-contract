package org.ensodai.avalonmediacard.contract.sync

import kotlinx.serialization.Serializable

@Serializable
enum class SyncStatus {
    PENDING, FAILED, SUCCESS
}
