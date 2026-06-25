package org.ensodai.avalonmediacard.contract.model

import org.ensodai.avalonmediacard.contract.MediaMetadata
import org.ensodai.avalonmediacard.contract.PersonMetadata
import org.ensodai.avalonmediacard.contract.TmdbMovieDto
import org.ensodai.avalonmediacard.contract.TmdbMultiSearchDto

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
