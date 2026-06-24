package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
data class WidgetSettingsData(
    val widgetId: String,
    val isVisible: Boolean,
    val orderIndex: Int,
    val widthSpan: Int
)
