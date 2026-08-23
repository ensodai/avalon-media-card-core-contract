package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieDto(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    val overview: String? = null
) {
    fun displayTitle(titleMode: TitleDisplayMode = TitleDisplayMode.LOCALIZED): String {
        return when (titleMode) {
            TitleDisplayMode.ORIGINAL -> originalTitle ?: originalName ?: title ?: name ?: ""
            TitleDisplayMode.LOCALIZED -> title ?: name ?: originalTitle ?: originalName ?: ""
        }
    }
}

@Serializable
data class TmdbMultiSearchDto(
    val id: Int,
    @SerialName("media_type")
    val mediaType: String, // "movie", "tv", "person"
    val title: String? = null,
    val name: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("original_name")
    val originalName: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    val overview: String? = null
) {
    fun displayTitle(titleMode: TitleDisplayMode = TitleDisplayMode.LOCALIZED): String {
        return when (titleMode) {
            TitleDisplayMode.ORIGINAL -> originalTitle ?: originalName ?: title ?: name ?: ""
            TitleDisplayMode.LOCALIZED -> title ?: name ?: originalTitle ?: originalName ?: ""
        }
    }
}
