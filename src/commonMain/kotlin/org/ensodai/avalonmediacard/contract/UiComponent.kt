package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed class UiComponent {
    abstract val id: Uuid?
    abstract val modifiers: List<UiModifier>
}

@Serializable
data class UiColumn(
    val children: List<UiComponent>,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiRow(
    val children: List<UiComponent>,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiBox(
    val children: List<UiComponent>,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiText(
    val text: String,
    val style: UiTextStyle = UiTextStyle.Body,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiImage(
    val url: String,
    val aspectRatio: Float? = null,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiButton(
    val text: String,
    val action: UiAction,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiLazyRow(
    val children: List<UiComponent>,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiSpacer(
    val sizeDp: Int,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

interface UiWidget {
    val widgetId: String
    val title: String
    val defaultSpan: Int
}

@Serializable
data class UiWidgetContainer(
    override val widgetId: String,
    override val title: String,
    override val defaultSpan: Int = 2, // Ширина в колонках от 1 до 4
    val children: List<UiComponent>,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent(), UiWidget

@Serializable
data class MovieCarouselItem(
    val mediaId: String,
    val catalogId: String,
    val title: String,
    val posterUrl: String
)

@Serializable
data class UiMovieCarousel(
    override val widgetId: String,
    override val title: String,
    val items: List<MovieCarouselItem>,
    val loadMoreAction: UiAction? = null,
    val titleAction: UiAction? = null,
    override val defaultSpan: Int = 4, // По умолчанию занимает всю ширину
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent(), UiWidget

@Serializable
data class UiMovieGrid(
    override val widgetId: String,
    val items: List<MovieCarouselItem>,
    val loadMoreAction: UiAction? = null,
    override val defaultSpan: Int = 4,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent(), UiWidget {
    override val title: String = ""
}

@Serializable
data class UiMediaHeader(
    val title: String,
    val subtitle: String? = null,
    val tagline: String? = null,
    val rating: Double? = null,
    val genres: List<String> = emptyList(),
    val releaseDate: String? = null,
    val posterUrl: String? = null,
    val backgroundUrl: String? = null,
    val directorName: String? = null,
    val directorId: String? = null,
    val directorImageUrl: String? = null,
    val isLoading: Boolean = false,
    val catalogId: String? = null,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiPersonHeader(
    val name: String,
    val knownForDepartment: String? = null,
    val birthday: String? = null,
    val deathday: String? = null,
    val placeOfBirth: String? = null,
    val profileUrl: String? = null,
    val isLoading: Boolean = false,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiTextSection(
    val title: String,
    val text: String,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

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

@Serializable
data class CastMemberItem(
    val id: String,
    val name: String,
    val character: String? = null,
    val profileUrl: String? = null,
    val catalogId: String
)

@Serializable
data class UiMediaCast(
    val title: String = "В главных ролях",
    val members: List<CastMemberItem> = emptyList(),
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

@Serializable
data class UiImageGallery(
    val title: String = "Фотографии",
    val imageUrls: List<String> = emptyList(),
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()

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
