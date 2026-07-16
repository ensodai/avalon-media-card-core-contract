package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable


@Serializable
data class LayoutNode(
    val nodeId: String,
    val slotId: SlotId
)

@Serializable
data class ScreenManifest(
    val slots: List<SlotId>
)

@Serializable
data class GlobalManifest(
    val screens: Map<String, ScreenManifest>
)
