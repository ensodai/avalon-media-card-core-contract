package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable

@Serializable
sealed interface SlotState {
    @Serializable
    data class Loading(val message: String? = null) : SlotState

    @Serializable
    data class Content(val data: SlotData) : SlotState

    @Serializable
    data class Error(val message: String, val retryAction: ServerAction? = null) : SlotState
    
    @Serializable
    data object Empty : SlotState
}

@Serializable
data class SlotUpdate(
    val slot: SlotId,
    val pluginId: String,
    val state: SlotState
)

@Serializable
sealed interface ScreenStreamEvent {
    @Serializable
    data class Manifest(val slots: List<SlotId>) : ScreenStreamEvent
    
    @Serializable
    data class Update(val update: SlotUpdate) : ScreenStreamEvent
}
