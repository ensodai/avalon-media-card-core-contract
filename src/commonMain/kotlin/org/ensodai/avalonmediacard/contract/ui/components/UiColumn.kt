package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiModifier
import kotlin.uuid.Uuid

@Serializable
data class UiColumn(
    val children: List<UiComponent>,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()
