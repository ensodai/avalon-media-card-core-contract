package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable

@Serializable
data class WidgetSettingsData(
    val widgetId: String,
    val isVisible: Boolean,
    val orderIndex: Int,
    val widthSpan: Int
)
