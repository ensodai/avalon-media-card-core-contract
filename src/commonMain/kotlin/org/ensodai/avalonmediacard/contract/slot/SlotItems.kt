package org.ensodai.avalonmediacard.contract.slot

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.MediaKey

@Serializable
data class MediaRatingItem(
    val source: String,
    val value: String
)

@Serializable
data class CustomListItem(
    val id: String,
    val name: String,
    val isAdded: Boolean,
    val toggleAction: Action
)

@Serializable
data class ButtonItem(
    val label: String = "",
    val icon: IconType? = null,
    val action: Action? = null,
    val customLists: List<CustomListItem>? = null,
    val createListActionTemplate: Action? = null
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
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val badges: List<String> = emptyList()
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
