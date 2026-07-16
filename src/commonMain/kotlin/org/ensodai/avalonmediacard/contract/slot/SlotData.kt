package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.MediaKey
import org.ensodai.avalonmediacard.contract.plugins.MediaStream
import org.ensodai.avalonmediacard.contract.model.ClickstreamContext

@Serializable
data class GenreItem(
    val id: String,
    val name: String,
    val clickAction: Action? = null
)

@Serializable
sealed interface SlotData {

    @Serializable 
    data class Header(
        val title: String,
        val originalTitle: String? = null,
        val subtitle: String? = null,
        val tagline: String? = null,
        val rating: Double? = null,
        val ratings: List<MediaRatingItem> = emptyList(),
        val genres: List<GenreItem> = emptyList(),
        val releaseDate: String? = null,
        val posterUrl: String? = null,
        val backgroundUrl: String? = null,
        val directorName: String? = null,
        val directorId: String? = null,
        val directorImageUrl: String? = null,
        val trailerUrl: String? = null,
        val mediaType: String? = null,
        val status: String? = null
    ) : SlotData

    @Serializable 
    data class ButtonGroup(val buttons: List<ButtonItem>) : SlotData

    @Serializable 
    data class Text(val content: String) : SlotData

    @Serializable 
    data class Cast(val title: String, val members: List<CastMemberItem>) : SlotData

    @Serializable 
    data class Carousel(
        val id: String, 
        val title: String, 
        val items: List<MovieCarouselItem>,
        val loadMoreAction: Action? = null,
        val titleAction: Action? = null,
        val telemetryContext: ClickstreamContext? = null
    ) : SlotData

    @Serializable
    data class Hero(
        val id: String,
        val title: String,
        val subtitle: String? = null,
        val items: List<MovieCarouselItem>,
        val telemetryContext: ClickstreamContext? = null
    ) : SlotData

    @Serializable
    data class CarouselBackdrops(
        val id: String,
        val title: String,
        val items: List<MovieCarouselItem>,
        val loadMoreAction: Action? = null,
        val titleAction: Action? = null,
        val telemetryContext: ClickstreamContext? = null
    ) : SlotData

    @Serializable
    data class Exploration(
        val id: String,
        val title: String,
        val items: List<MovieCarouselItem>,
        val loadMoreAction: Action? = null,
        val telemetryContext: ClickstreamContext? = null
    ) : SlotData

    @Serializable 
    data class Comments(
        val title: String, 
        val comments: List<CommentItem>,
        val loadMoreAction: Action? = null,
        val currentPage: Int = 1
    ) : SlotData
    
    @Serializable
    data class ContinueWatching(
        val mediaId: String,
        val title: String,
        val subtitle: String? = null,
        val progressPercent: Float? = null,
        val playAction: Action? = null
    ) : SlotData
    
    @Serializable
    data class UserActions(
        val mediaKey: MediaKey,
        val currentStatus: String,
        val currentRating: Int?,
        val maxRating: Int = 10,
        val statusOptions: Map<String, Action> = emptyMap(),
        val ratingOptions: Map<Int, Action> = emptyMap()
    ) : SlotData
    
    @Serializable
    data class SyncStatus(
        val statusText: String,
        val isSyncing: Boolean,
        val lastSyncText: String? = null
    ) : SlotData

    @Serializable
    data class Grid(
        val id: String,
        val title: String? = null,
        val items: List<MovieCarouselItem>,
        val loadMoreAction: Action? = null,
        val telemetryContext: ClickstreamContext? = null
    ) : SlotData

    @Serializable
    data class TvSeasons(
        val seasons: List<SeasonItem>,
        val selectedSeasonNumber: Int,
        val seasonContents: Map<Int, SeasonContent>
    ) : SlotData

    @Serializable
    data class SettingsGroup(
        val title: String,
        val description: String? = null,
        val fields: List<SettingField>,
        val saveAction: Action? = null,
        val saveActionLabel: String? = null,
        val isSaveEnabled: Boolean = true,
        val connectionStatus: ValidationStatus = ValidationStatus.None
    ) : SlotData

    @Serializable
    data class MediaSources(
        val sources: List<MediaStream>,
        val mediaKey: MediaKey? = null
    ) : SlotData

    @Serializable
    data class TorrentInspector(
        val torrentHash: String,
        val torrentTitle: String,
        val files: List<TorrentFileItem>
    ) : SlotData
}

@Serializable
enum class ValidationStatus {
    None,
    Pending,
    Success,
    Error
}

@Serializable
sealed interface SettingField {
    val key: String
    val label: String

    @Serializable
    data class Toggle(
        override val key: String,
        override val label: String,
        val value: Boolean,
        val onChangeAction: Action? = null
    ) : SettingField

    @Serializable
    data class TextField(
        override val key: String,
        override val label: String,
        val value: String,
        val placeholder: String? = null,
        val isSensitive: Boolean = false,
        val isEnabled: Boolean = true,
        val validateAction: Action? = null,
        val validationStatus: ValidationStatus = ValidationStatus.None,
        val validationMessage: String? = null
    ) : SettingField

    @Serializable
    data class Info(
        override val key: String,
        override val label: String,
        val description: String,
        val action: Action? = null,
        val actionLabel: String? = null
    ) : SettingField
}

@Serializable
data class SeasonItem(
    val id: String,
    val seasonNumber: Int,
    val name: String,
    val episodeCount: Int,
    val isFullyWatched: Boolean = false,
    val isWatching: Boolean = false,
    val selectAction: Action? = null,
    val markWatchedAction: Action? = null
)

@Serializable
data class SeasonContent(
    val isLoading: Boolean,
    val episodes: List<EpisodeItem>?
)

@Serializable
data class EpisodeItem(
    val id: String,
    val episodeNumber: Int,
    val name: String,
    val overview: String?,
    val stillUrl: String?,
    val airDate: String?,
    val voteAverage: Double?,
    val runtime: Int?,
    val isWatched: Boolean = false,
    val userRating: Int? = null,
    val playAction: Action? = null,
    val toggleWatchedAction: Action? = null
)

@Serializable
data class TorrentFileItem(
    val path: String,
    val size: Long,
    val isVideo: Boolean,
    val mappedSeasons: List<Int>?,
    val mappedEpisodes: List<Int>?,
    val fileIndex: Int? = null,
    val remapAction: Action? = null
)
