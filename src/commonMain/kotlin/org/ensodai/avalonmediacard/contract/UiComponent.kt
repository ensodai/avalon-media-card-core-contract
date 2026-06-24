package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed class UiComponent {
    abstract val id: Uuid?
    abstract val modifiers: List<UiModifier>
}

interface UiWidget {
    val widgetId: String
    val title: String
    val defaultSpan: Int
}

// --- Стили Текста ---

@Serializable
enum class UiTextStyle {
    Title,      // Большой заголовок (для карточки фильма, названия экрана)
    Subtitle,   // Подзаголовок (жанр, год)
    Body,       // Обычный текст описания
    Caption     // Мелкий служебный текст (размер файла, сиды)
}

// --- Модификаторы ---

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

// --- Действия (Actions) ---

@Serializable
sealed class UiAction

@Serializable
data class UiActionNavigate(
    val screen: Screen
) : UiAction()

@Serializable
data class UiActionPlayVideo(
    val title: String,
    val streamUrl: String
) : UiAction()

@Serializable
data class UiActionOpenUrl(
    val url: String
) : UiAction()

@Serializable
data class UiActionSearch(
    val query: String
) : UiAction()

@Serializable
data class UiActionCustom(
    val pluginId: String,
    val actionId: String,
    val payload: Map<String, String> = emptyMap()
) : UiAction()

@Serializable
data class UiActionLoadMore(
    val pluginId: String,
    val widgetId: String,
    val page: Int
) : UiAction()

@Serializable
data class UiActionCommand(
    val pluginId: String,
    val commandClass: String,
    val payloadJson: String
) : UiAction()

@Serializable
data class UiActionSetRating(
    val mediaId: String,
    val rating: Int
) : UiAction()

@Serializable
data class UiActionSetStatus(
    val mediaId: String,
    val status: MediaStatus
) : UiAction()
