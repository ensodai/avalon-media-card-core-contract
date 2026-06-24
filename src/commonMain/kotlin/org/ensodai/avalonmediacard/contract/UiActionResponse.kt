package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UiActionResponse(
    val success: Boolean,
    val message: String? = null,
    val nextAction: UiAction? = null,
    val componentUpdates: Map<Uuid, UiComponent> = emptyMap()
)
