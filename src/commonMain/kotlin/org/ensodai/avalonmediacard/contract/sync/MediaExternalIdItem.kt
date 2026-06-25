package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
data class MediaExternalIdItem(
    val id: String,
    val userMovieId: String,
    val externalSource: String, // "tmdb", "imdb", "tvdb", "myshows", "trakt"
    val externalId: String
)
