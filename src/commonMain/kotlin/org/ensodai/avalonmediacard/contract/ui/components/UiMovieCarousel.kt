package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.UiAction
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiModifier
import org.ensodai.avalonmediacard.contract.UiWidget
import kotlin.uuid.Uuid

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
