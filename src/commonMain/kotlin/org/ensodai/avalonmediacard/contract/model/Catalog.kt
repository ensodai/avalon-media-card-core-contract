package org.ensodai.avalonmediacard.contract.model

import org.ensodai.avalonmediacard.contract.*

/**
 * Интерфейс каталога медиаданных. Реализуется плагином источника (например, TMDB).
 */
interface MediaCatalog {
    suspend fun getTrending(page: Int): List<TmdbMovieDto>
    suspend fun getTopRated(page: Int): List<TmdbMovieDto>
    suspend fun getUpcoming(page: Int): List<TmdbMovieDto>
    suspend fun getTrendingShows(page: Int): List<TmdbMovieDto>
    suspend fun getPopularShows(page: Int): List<TmdbMovieDto>
    suspend fun getTopRatedShows(page: Int): List<TmdbMovieDto>
    suspend fun getRecommendations(key: MediaKey, page: Int): List<TmdbMovieDto>
    suspend fun getSimilar(key: MediaKey, page: Int): List<TmdbMovieDto>
    suspend fun searchMedia(query: String, page: Int): List<TmdbMultiSearchDto>
    suspend fun getMediaDetails(key: MediaKey, requireSeasons: Boolean = true, requireVideos: Boolean = true): MediaMetadata
    suspend fun getPersonDetails(key: MediaKey): PersonMetadata
    suspend fun getSeasonDetails(key: MediaKey, seasonNumber: Int): List<org.ensodai.avalonmediacard.contract.slot.EpisodeItem>
    suspend fun discoverMedia(genres: List<Int>, keywords: List<Int>, page: Int = 1, isTv: Boolean = false): List<TmdbMovieDto>
    suspend fun discoverMediaByParams(params: Map<String, String>, targetType: EntityType, page: Int = 1): List<TmdbMovieDto>
}
