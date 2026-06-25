package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiModifier
import kotlin.uuid.Uuid

@Serializable
data class CastMemberItem(
    val key: MediaKey,
    val name: String,
    val character: String? = null,
    val profileUrl: String? = null
)

@Serializable
data class UiMediaCast(
    val title: String = "В главных ролях",
    val members: List<CastMemberItem> = emptyList(),
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()
