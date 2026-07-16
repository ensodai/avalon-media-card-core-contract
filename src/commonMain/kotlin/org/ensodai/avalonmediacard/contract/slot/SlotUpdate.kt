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
    val slotId: SlotId,
    val nodeId: String,
    val state: SlotState
)

@Serializable
sealed interface ScreenStreamEvent {
    @Serializable
    data class Layout(val nodes: List<LayoutNode>) : ScreenStreamEvent

    @Serializable
    data class Update(val update: SlotUpdate) : ScreenStreamEvent
}
