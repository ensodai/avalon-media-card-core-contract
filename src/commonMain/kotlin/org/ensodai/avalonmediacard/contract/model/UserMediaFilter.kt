package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface UserMediaFilter {
    @Serializable
    data class ByType(
        val mediaType: MediaType
    ) : UserMediaFilter

    @Serializable
    data class ByStatus(
        val mediaType: MediaType,
        val status: MediaStatus
    ) : UserMediaFilter

    @Serializable
    data class ByGenre(
        val genreId: Int
    ) : UserMediaFilter

    @Serializable
    data class ByRating(
        val rating: Int
    ) : UserMediaFilter

    @Serializable
    data class ByDecade(
        val decadeKey: String
    ) : UserMediaFilter
}
