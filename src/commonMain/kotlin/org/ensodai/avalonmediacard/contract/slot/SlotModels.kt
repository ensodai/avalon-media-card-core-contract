package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.MediaKey
import org.ensodai.avalonmediacard.contract.Screen

@Serializable
enum class SlotId {
    Header,
    PlayButtons,
    CollectionButtons,
    ContinueWatching,
    UserActions,
    SyncStatus,
    Description,
    Cast,
    Carousels,
    Comments,
    Sidebar,
    HomeWidget,
    Integrations,
    PersonHeader,
    PersonImages,
    PersonBio,
    PersonCredits,
    MediaList
}

@Serializable
sealed interface Action

@Serializable 
data class ActionNavigate(val screen: Screen) : Action

@Serializable 
data class ActionPlayVideo(val url: String, val title: String) : Action

@Serializable 
data class ActionOpenUrl(val url: String) : Action

@Serializable 
data class ActionCommand(
    val pluginId: String,
    val commandId: String,
    val payload: String = "{}"
) : Action

@Serializable
data class MediaRating(
    val source: String,
    val value: String,
    val colorHex: String? = null
)

@Serializable
data class ButtonModel(
    val label: String,
    val icon: String? = null,
    val action: Action
)

@Serializable
data class CastMemberItem(
    val key: MediaKey,
    val name: String,
    val character: String? = null,
    val profileUrl: String? = null
)

@Serializable
data class MovieCarouselItem(
    val key: MediaKey,
    val title: String,
    val posterUrl: String? = null
)

@Serializable
data class CommentItem(
    val id: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val commentText: String,
    val likesCount: Int = 0,
    val isSpoiler: Boolean = false,
    val dateText: String? = null,
    val userRating: Int? = null
)

@Serializable
sealed interface SlotData {

    @Serializable 
    data class Header(
        val title: String,
        val subtitle: String? = null,
        val tagline: String? = null,
        val rating: Double? = null,
        val ratings: List<MediaRating> = emptyList(),
        val genres: List<String> = emptyList(),
        val releaseDate: String? = null,
        val posterUrl: String? = null,
        val backgroundUrl: String? = null,
        val directorName: String? = null,
        val directorId: String? = null,
        val directorImageUrl: String? = null,
        val trailerUrl: String? = null
    ) : SlotData

    @Serializable 
    data class ButtonGroup(val buttons: List<ButtonModel>) : SlotData

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
        val titleAction: Action? = null
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
        val statusText: String? = null,
        val statusAction: Action? = null,
        val userRating: Int? = null,
        val ratingAction: Action? = null
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
        val loadMoreAction: Action? = null
    ) : SlotData
}

@Serializable
data class SlotUpdate(
    val slot: SlotId,
    val pluginId: String,
    val data: SlotData?,
    val isLoading: Boolean = false
)
