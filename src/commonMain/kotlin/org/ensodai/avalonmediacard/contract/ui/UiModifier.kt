package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
sealed class UiModifier

@Serializable
data class UiModifierPadding(
    val start: Int = 0,
    val top: Int = 0,
    val end: Int = 0,
    val bottom: Int = 0
) : UiModifier()

@Serializable
data class UiModifierSize(
    val width: Int? = null,  // В Dp, null означает wrap_content
    val height: Int? = null
) : UiModifier()

@Serializable
data class UiModifierBackground(
    val colorHex: String  // Например, "#FF121212"
) : UiModifier()

@Serializable
data class UiModifierClickable(
    val action: UiAction
) : UiModifier()

@Serializable
data class UiModifierClipRounded(
    val cornerRadiusDp: Int
) : UiModifier()
