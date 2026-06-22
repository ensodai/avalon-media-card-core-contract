package org.ensodai.avalonmediacard.contract

import kotlin.uuid.Uuid

@DslMarker
annotation class SduiDsl

@SduiDsl
interface UiContainerBuilder {
    fun add(component: UiComponent)

    fun column(id: Uuid? = null, block: ColumnBuilder.() -> Unit) {
        add(ColumnBuilder(id).apply(block).build())
    }

    fun row(id: Uuid? = null, block: RowBuilder.() -> Unit) {
        add(RowBuilder(id).apply(block).build())
    }

    fun box(id: Uuid? = null, block: BoxBuilder.() -> Unit) {
        add(BoxBuilder(id).apply(block).build())
    }

    fun text(
        text: String,
        style: UiTextStyle = UiTextStyle.Body,
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiText(text = text, style = style, id = id, modifiers = modifiers))
    }

    fun image(
        url: String,
        aspectRatio: Float? = null,
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiImage(url = url, aspectRatio = aspectRatio, id = id, modifiers = modifiers))
    }

    fun spacer(sizeDp: Int) {
        add(UiSpacer(sizeDp = sizeDp))
    }

    fun button(
        text: String,
        action: UiAction,
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiButton(text = text, action = action, id = id, modifiers = modifiers))
    }

    fun lazyRow(id: Uuid? = null, block: LazyRowBuilder.() -> Unit) {
        add(LazyRowBuilder(id).apply(block).build())
    }

    fun widget(
        widgetId: String,
        title: String,
        defaultSpan: Int = 2,
        id: Uuid? = null,
        block: ColumnBuilder.() -> Unit
    ) {
        val childrenList = ColumnBuilder(null).apply(block).build().children
        add(UiWidgetContainer(widgetId, title, defaultSpan, childrenList, id))
    }

    fun movieCarousel(
        widgetId: String,
        title: String,
        items: List<MovieCarouselItem>,
        loadMoreAction: UiAction? = null,
        titleAction: UiAction? = null,
        defaultSpan: Int = 4,
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiMovieCarousel(widgetId, title, items, loadMoreAction, titleAction, defaultSpan, id, modifiers))
    }

    fun movieGrid(
        widgetId: String,
        items: List<MovieCarouselItem>,
        loadMoreAction: UiAction? = null,
        defaultSpan: Int = 4,
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiMovieGrid(widgetId, items, loadMoreAction, defaultSpan, id, modifiers))
    }

    fun mediaHeader(
        title: String,
        subtitle: String? = null,
        tagline: String? = null,
        rating: Double? = null,
        ratings: List<MediaRating> = emptyList(),
        genres: List<String> = emptyList(),
        releaseDate: String? = null,
        posterUrl: String? = null,
        backgroundUrl: String? = null,
        directorName: String? = null,
        directorId: String? = null,
        directorImageUrl: String? = null,
        trailerUrl: String? = null,
        isLoading: Boolean = false,
        catalogId: String? = null,
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiMediaHeader(title, subtitle, tagline, rating, ratings, genres, releaseDate, posterUrl, backgroundUrl, directorName, directorId, directorImageUrl, trailerUrl, isLoading, catalogId, id, modifiers))
    }

    fun personHeader(
        name: String,
        knownForDepartment: String? = null,
        birthday: String? = null,
        deathday: String? = null,
        placeOfBirth: String? = null,
        profileUrl: String? = null,
        isLoading: Boolean = false,
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiPersonHeader(name, knownForDepartment, birthday, deathday, placeOfBirth, profileUrl, isLoading, id, modifiers))
    }
    fun textSection(
        title: String,
        text: String,
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiTextSection(title, text, id, modifiers))
    }

    fun mediaTrailers(
        title: String = "Трейлеры и доп. материалы",
        videos: List<TrailerItem> = emptyList(),
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiMediaTrailers(title, videos, id, modifiers))
    }

    fun mediaCast(
        title: String = "В главных ролях",
        members: List<CastMemberItem> = emptyList(),
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiMediaCast(title, members, id, modifiers))
    }

    fun imageGallery(
        title: String = "Фотографии",
        imageUrls: List<String> = emptyList(),
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiImageGallery(title, imageUrls, id, modifiers))
    }

    fun stateWidget(
        widgetId: String,
        title: String,
        state: WidgetState,
        expectedType: WidgetType,
        defaultSpan: Int = 4,
        id: Uuid? = null,
        block: (ModifierBuilder.() -> Unit)? = null
    ) {
        val modifiers = block?.let { ModifierBuilder().apply(it).build() } ?: emptyList()
        add(UiStateWidget(widgetId, title, state, expectedType, defaultSpan, id, modifiers))
    }
}

class ColumnBuilder(private val id: Uuid?) : UiContainerBuilder {
    private val children = mutableListOf<UiComponent>()
    private val modifiers = mutableListOf<UiModifier>()

    override fun add(component: UiComponent) {
        children.add(component)
    }

    fun padding(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) {
        modifiers.add(UiModifierPadding(start, top, end, bottom))
    }

    fun size(width: Int? = null, height: Int? = null) {
        modifiers.add(UiModifierSize(width, height))
    }

    fun clickable(action: UiAction) {
        modifiers.add(UiModifierClickable(action))
    }

    fun clip(cornerRadiusDp: Int) {
        modifiers.add(UiModifierClipRounded(cornerRadiusDp))
    }

    fun background(colorHex: String) {
        modifiers.add(UiModifierBackground(colorHex))
    }

    fun build() = UiColumn(id = id, children = children, modifiers = modifiers)
}

class RowBuilder(private val id: Uuid?) : UiContainerBuilder {
    private val children = mutableListOf<UiComponent>()
    private val modifiers = mutableListOf<UiModifier>()

    override fun add(component: UiComponent) {
        children.add(component)
    }

    fun padding(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) {
        modifiers.add(UiModifierPadding(start, top, end, bottom))
    }

    fun size(width: Int? = null, height: Int? = null) {
        modifiers.add(UiModifierSize(width, height))
    }

    fun clickable(action: UiAction) {
        modifiers.add(UiModifierClickable(action))
    }

    fun clip(cornerRadiusDp: Int) {
        modifiers.add(UiModifierClipRounded(cornerRadiusDp))
    }

    fun background(colorHex: String) {
        modifiers.add(UiModifierBackground(colorHex))
    }

    fun build() = UiRow(id = id, children = children, modifiers = modifiers)
}

class BoxBuilder(private val id: Uuid?) : UiContainerBuilder {
    private val children = mutableListOf<UiComponent>()
    private val modifiers = mutableListOf<UiModifier>()

    override fun add(component: UiComponent) {
        children.add(component)
    }

    fun padding(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) {
        modifiers.add(UiModifierPadding(start, top, end, bottom))
    }

    fun size(width: Int? = null, height: Int? = null) {
        modifiers.add(UiModifierSize(width, height))
    }

    fun clickable(action: UiAction) {
        modifiers.add(UiModifierClickable(action))
    }

    fun clip(cornerRadiusDp: Int) {
        modifiers.add(UiModifierClipRounded(cornerRadiusDp))
    }

    fun background(colorHex: String) {
        modifiers.add(UiModifierBackground(colorHex))
    }

    fun build() = UiBox(id = id, children = children, modifiers = modifiers)
}

class LazyRowBuilder(private val id: Uuid?) : UiContainerBuilder {
    private val children = mutableListOf<UiComponent>()
    private val modifiers = mutableListOf<UiModifier>()

    override fun add(component: UiComponent) {
        children.add(component)
    }

    fun build() = UiLazyRow(id = id, children = children, modifiers = modifiers)
}

class ModifierBuilder {
    private val modifiers = mutableListOf<UiModifier>()

    fun padding(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0) {
        modifiers.add(UiModifierPadding(start, top, end, bottom))
    }

    fun size(width: Int? = null, height: Int? = null) {
        modifiers.add(UiModifierSize(width, height))
    }

    fun clickable(action: UiAction) {
        modifiers.add(UiModifierClickable(action))
    }

    fun clip(cornerRadiusDp: Int) {
        modifiers.add(UiModifierClipRounded(cornerRadiusDp))
    }

    fun build() = modifiers
}

fun buildUi(block: UiContainerBuilder.() -> Unit): List<UiComponent> {
    val root = object : UiContainerBuilder {
        val components = mutableListOf<UiComponent>()
        override fun add(component: UiComponent) {
            components.add(component)
        }
    }
    root.block()
    return root.components
}
