package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.UiAction
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiModifier
import org.ensodai.avalonmediacard.contract.UiWidget
import kotlin.uuid.Uuid

@Serializable
data class UiMovieGrid(
    override val widgetId: String,
    val items: List<MovieCarouselItem>,
    val loadMoreAction: UiAction? = null,
    override val defaultSpan: Int = 4,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList(),
    override val title: String = ""
) : UiComponent(), UiWidget
