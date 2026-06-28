package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable

@Serializable
data class SlotUpdate(
    val slot: SlotId,
    val pluginId: String,
    val data: SlotData?,
    val isLoading: Boolean = false
)
