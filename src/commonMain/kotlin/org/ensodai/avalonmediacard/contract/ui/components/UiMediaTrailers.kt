package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiModifier
import kotlin.uuid.Uuid

@Serializable
data class TrailerItem(
    val name: String,
    val videoUrl: String,
    val type: String? = null
)

@Serializable
data class UiMediaTrailers(
    val title: String = "Трейлеры и доп. материалы",
    val videos: List<TrailerItem> = emptyList(),
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()
