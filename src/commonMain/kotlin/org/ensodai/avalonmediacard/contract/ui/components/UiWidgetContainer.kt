package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiModifier
import org.ensodai.avalonmediacard.contract.UiWidget
import kotlin.uuid.Uuid

@Serializable
data class UiWidgetContainer(
    override val widgetId: String,
    override val title: String,
    override val defaultSpan: Int = 2, // Ширина в колонках от 1 до 4
    val children: List<UiComponent>,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent(), UiWidget
