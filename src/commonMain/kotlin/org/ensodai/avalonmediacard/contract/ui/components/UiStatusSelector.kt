package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.MediaStatus
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiModifier
import kotlin.uuid.Uuid

@Serializable
data class StatusOption(
    val status: MediaStatus,
    val label: String,
    val iconName: String
)

@Serializable
data class UiStatusSelector(
    val mediaId: String,
    val currentStatus: MediaStatus?,
    val options: List<StatusOption>,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()
