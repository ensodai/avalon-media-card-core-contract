package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
data class ActorMetadata(
    val name: String,
    val character: String? = null,
    val profileUrl: String? = null,
    val id: String? = null
)

@Serializable
data class TrailerMetadata(
    val name: String,
    val videoUrl: String,
    val type: String? = null
)

@Serializable
data class RelatedMediaMetadata(
    val mediaId: String,
    val title: String,
    val posterUrl: String? = null
)

@Serializable
data class SeasonMetadata(
    val id: String,
    val seasonNumber: Int,
    val name: String,
    val overview: String? = null,
    val posterUrl: String? = null,
    val episodeCount: Int = 0,
    val airDate: String? = null
)

@Serializable
data class MediaMetadata(
    val title: String,
    val originalTitle: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val posterUrl: String? = null,
    val backgroundUrl: String? = null,
    val rating: String? = null,
    val genres: List<String> = emptyList(),
    val releaseDate: String? = null,
    val tagline: String? = null,
    val director: String? = null,
    val directorImageUrl: String? = null,
    val directorId: String? = null,
    val cast: List<ActorMetadata> = emptyList(),
    val trailers: List<TrailerMetadata> = emptyList(),
    val recommendations: List<RelatedMediaMetadata> = emptyList(),
    val similar: List<RelatedMediaMetadata> = emptyList(),
    
    // TV Show specific metadata
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
    val status: String? = null,
    val network: String? = null,
    val seasons: List<SeasonMetadata> = emptyList()
)
