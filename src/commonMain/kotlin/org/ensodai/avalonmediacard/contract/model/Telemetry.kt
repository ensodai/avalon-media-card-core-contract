package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

enum class ClickstreamEventType {
    CLICK,
    DWELL,
    SEARCH,
    PAGE_VIEW,
    IMPRESSION_BATCH,
    PLAYBACK_STOP,
    SCROLL
}

enum class ClickstreamTargetType {
    MEDIA_MOVIE,
    MEDIA_TV,
    PERSON,
    GENRE,
    UI_ELEMENT
}

enum class ClickstreamContext {
    HERO_BANNER,
    CAROUSEL_CONTINUE,
    CAROUSEL_DISCOVER,
    CAROUSEL_PERSON,
    CAROUSEL_DETAILS_SIMILAR,
    CAROUSEL_DETAILS_RECOMMENDATIONS,
    DETAILS_PAGE,
    SEARCH_PAGE,
    HOME_PAGE
}

@Serializable
sealed class ClickstreamPayload {
    @Serializable
    @SerialName("details_navigation")
    data class DetailsNavigation(val sourceMediaId: String) : ClickstreamPayload()

    @Serializable
    @SerialName("search")
    data class SearchEvent(val query: String) : ClickstreamPayload()

    @Serializable
    @SerialName("carousel_interaction")
    data class CarouselInteraction(val positionIndex: Int) : ClickstreamPayload()
    
    @Serializable
    data class ImpressionItem(val id: String, val targetType: ClickstreamTargetType)

    @Serializable
    @SerialName("impression_batch")
    data class ImpressionBatch(val items: List<ImpressionItem>) : ClickstreamPayload()

    @Serializable
    @SerialName("playback_stop")
    data class PlaybackStop(val completionPercentage: Double) : ClickstreamPayload()

    @Serializable
    @SerialName("scroll_depth")
    data class ScrollDepth(val maxScrollDepthPercentage: Double) : ClickstreamPayload()

    @Serializable
    @SerialName("empty")
    data object Empty : ClickstreamPayload()
}

@Serializable
data class TelemetryEvent(
    val eventType: ClickstreamEventType,
    val targetType: ClickstreamTargetType? = null,
    val targetId: String? = null,
    val context: ClickstreamContext,
    val dwellTimeMs: Long = 0,
    val payload: ClickstreamPayload = ClickstreamPayload.Empty,
    val timestamp: Instant? = null
)

