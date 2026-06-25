package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UiStateWidget(
    override val widgetId: String,
    override val title: String,
    val state: WidgetState,
    val expectedType: WidgetType,
    override val defaultSpan: Int = 4,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent(), UiWidget
