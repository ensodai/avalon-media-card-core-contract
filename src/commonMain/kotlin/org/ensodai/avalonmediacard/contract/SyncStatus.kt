package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
enum class SyncStatus {
    PENDING, FAILED, SUCCESS
}
