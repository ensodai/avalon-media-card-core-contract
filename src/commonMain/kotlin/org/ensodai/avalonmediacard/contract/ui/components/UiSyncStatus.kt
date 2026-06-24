package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UiSyncStatus(
    val mediaId: String,
    val statuses: List<SyncServiceStatus>,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()
