package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.MediaKey

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
    data class MediaHeader(val key: MediaKey) : WidgetTarget

    @Serializable
    data class MediaDescription(val key: MediaKey) : WidgetTarget

    @Serializable
    data class MediaTrailers(val key: MediaKey) : WidgetTarget

    @Serializable
    data class MediaCast(val key: MediaKey) : WidgetTarget

    @Serializable
    data class MediaRecommendations(val key: MediaKey) : WidgetTarget

    @Serializable
    data class MediaSimilar(val key: MediaKey) : WidgetTarget

    @Serializable
    data class MediaUserActions(val key: MediaKey) : WidgetTarget

    // --- Экран деталей персоны (актера/режиссера) ---
    @Serializable
    data class PersonHeader(val key: MediaKey) : WidgetTarget

    @Serializable
    data class PersonImages(val key: MediaKey) : WidgetTarget

    @Serializable
    data class PersonBio(val key: MediaKey) : WidgetTarget

    @Serializable
    data class PersonActingCredits(val key: MediaKey) : WidgetTarget

    @Serializable
    data class PersonDirectingCredits(val key: MediaKey) : WidgetTarget

    @Serializable
    data class MediaList(val key: MediaKey, val listType: String) : WidgetTarget

    // Резервный кастом для непредвиденных интеграций сторонних плагинов
    @Serializable
    data class Custom(val name: String, val params: Map<String, String> = emptyMap()) : WidgetTarget
}
