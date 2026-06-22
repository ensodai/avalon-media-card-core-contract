package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieDto(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    val poster_path: String? = null,
    val backdrop_path: String? = null,
    val release_date: String? = null,
    val vote_average: Double? = null,
    val overview: String? = null
)

@Serializable
data class TmdbMultiSearchDto(
    val id: Int,
    val media_type: String, // "movie", "tv", "person"
    val title: String? = null,
    val name: String? = null,
    val poster_path: String? = null,
    val backdrop_path: String? = null,
    val release_date: String? = null,
    val first_air_date: String? = null,
    val vote_average: Double? = null,
    val overview: String? = null
)

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
data class MediaMetadata(
    val title: String,
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
    val similar: List<RelatedMediaMetadata> = emptyList()
)

@Serializable
data class FilmographyCredit(
    val mediaId: String,
    val title: String,
    val posterUrl: String? = null,
    val releaseDate: String? = null,
    val mediaType: String, // "movie" or "tv"
    val department: String, // "Directing", "Writing", "Production", "Acting"
    val jobOrCharacter: String? = null
)

@Serializable
data class PersonMetadata(
    val name: String,
    val biography: String? = null,
    val birthday: String? = null,
    val deathday: String? = null,
    val placeOfBirth: String? = null,
    val profileUrl: String? = null,
    val knownForDepartment: String? = null,
    val externalIds: Map<String, String> = emptyMap(),
    val images: List<String> = emptyList(),
    val filmography: List<FilmographyCredit> = emptyList()
)

/**
 * Интерфейс каталога медиаданных. Реализуется плагином источника (например, TMDB).
 */
interface MediaCatalog {
    suspend fun getTrending(page: Int): List<TmdbMovieDto>
    suspend fun getTopRated(page: Int): List<TmdbMovieDto>
    suspend fun getUpcoming(page: Int): List<TmdbMovieDto>
    suspend fun getRecommendations(movieId: String, page: Int): List<TmdbMovieDto>
    suspend fun getSimilar(movieId: String, page: Int): List<TmdbMovieDto>
    suspend fun searchMedia(query: String, page: Int): List<TmdbMultiSearchDto>
    suspend fun getMediaDetails(mediaId: String): MediaMetadata
    suspend fun getPersonDetails(personId: String): PersonMetadata
}



