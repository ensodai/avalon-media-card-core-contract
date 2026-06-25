package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.serialization.Serializable

/**
 * Целевые виджеты интеграции компонентов плагина в интерфейс ядра.
 */
@Serializable
sealed interface WidgetTarget {
    @Serializable
    data object HomeWidget : WidgetTarget

    @Serializable
    data object Integrations : WidgetTarget

    @Serializable
    data class SearchResults(val query: String) : WidgetTarget

    // --- Экран деталей медиа (фильм/сериал) ---
    @Serializable
    data class MediaHeader(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaDescription(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaTrailers(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaCast(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaRecommendations(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaSimilar(val mediaId: String) : WidgetTarget

    @Serializable
    data class MediaUserActions(val mediaId: String) : WidgetTarget

    // --- Экран деталей персоны (актера/режиссера) ---
    @Serializable
    data class PersonHeader(val personId: String) : WidgetTarget

    @Serializable
    data class PersonImages(val personId: String) : WidgetTarget

    @Serializable
    data class PersonBio(val personId: String) : WidgetTarget

    @Serializable
    data class PersonActingCredits(val personId: String) : WidgetTarget

    @Serializable
    data class PersonDirectingCredits(val personId: String) : WidgetTarget

    @Serializable
    data class MediaList(val movieId: String, val listType: String) : WidgetTarget

    // Резервный кастом для непредвиденных интеграций сторонних плагинов
    @Serializable
    data class Custom(val name: String, val params: Map<String, String> = emptyMap()) : WidgetTarget
}
