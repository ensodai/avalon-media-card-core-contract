package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UiIntegrationCard(
    val service: String,
    val displayName: String,
    val description: String,
    val connected: Boolean,
    val username: String? = null,
    val comingSoon: Boolean = false,
    val hasSettings: Boolean = false,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()
