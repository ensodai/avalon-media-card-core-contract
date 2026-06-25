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

@Serializable
enum class UiTextStyle {
    Title,      // Большой заголовок (для карточки фильма, названия экрана)
    Subtitle,   // Подзаголовок (жанр, год)
    Body,       // Обычный текст описания
    Caption     // Мелкий служебный текст (размер файла, сиды)
}
