package org.ensodai.avalonmediacard.contract.model

import org.ensodai.avalonmediacard.contract.MediaKey
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
    suspend fun getRecommendations(key: MediaKey, page: Int): List<TmdbMovieDto>
    suspend fun getSimilar(key: MediaKey, page: Int): List<TmdbMovieDto>
    suspend fun searchMedia(query: String, page: Int): List<TmdbMultiSearchDto>
    suspend fun getMediaDetails(key: MediaKey): MediaMetadata
    suspend fun getPersonDetails(key: MediaKey): PersonMetadata
}
