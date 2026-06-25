package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
sealed interface MediaProvider {
    val id: String

    @Serializable
    data object Tmdb : MediaProvider {
        override val id: String = "tmdb"
    }

    @Serializable
    data object Youtube : MediaProvider {
        override val id: String = "youtube"
    }

    @Serializable
    data object Trakt : MediaProvider {
        override val id: String = "trakt"
    }

    @Serializable
    data class Custom(override val id: String) : MediaProvider
}

@Serializable
data class MediaKey(
    val provider: MediaProvider,
    val mediaId: String
)
