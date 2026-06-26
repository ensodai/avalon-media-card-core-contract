package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
enum class SyncAction {
    WATCH, UNWATCH, RATE, PROGRESS, COLLECT, UNCOLLECT
}
