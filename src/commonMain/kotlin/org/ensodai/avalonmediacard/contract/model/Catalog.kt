package org.ensodai.avalonmediacard.contract.model

/**
 * Интерфейс каталога медиаданных. Реализуется плагином источника (например, TMDB).
 */
interface MediaCatalog {
    suspend fun getTrending(page: Int, language: String = "ru"): List<TmdbMovieDto>
    suspend fun getTopRated(page: Int, language: String = "ru"): List<TmdbMovieDto>
    suspend fun getUpcoming(page: Int, language: String = "ru"): List<TmdbMovieDto>
    suspend fun getTrendingShows(page: Int, language: String = "ru"): List<TmdbMovieDto>
    suspend fun getPopularShows(page: Int, language: String = "ru"): List<TmdbMovieDto>
    suspend fun getTopRatedShows(page: Int, language: String = "ru"): List<TmdbMovieDto>
    suspend fun getRecommendations(key: MediaKey, page: Int, language: String = "ru"): List<TmdbMovieDto>
    suspend fun getSimilar(key: MediaKey, page: Int, language: String = "ru"): List<TmdbMovieDto>
    suspend fun searchMedia(query: String, page: Int, language: String = "ru"): List<TmdbMultiSearchDto>
    suspend fun getMediaDetails(key: MediaKey, requireSeasons: Boolean = true, requireVideos: Boolean = true, language: String = "ru"): MediaMetadata
    suspend fun getMediaDetailsBatch(keys: List<MediaKey>, requireSeasons: Boolean = true, requireVideos: Boolean = true, language: String = "ru"): Map<MediaKey, MediaMetadata>
    suspend fun getPersonDetails(key: MediaKey, language: String = "ru"): PersonMetadata
    suspend fun getSeasonDetails(key: MediaKey, seasonNumber: Int, language: String = "ru"): List<org.ensodai.avalonmediacard.contract.slot.EpisodeItem>
    suspend fun discoverMedia(genres: List<Int>, keywords: List<Int>, page: Int = 1, isTv: Boolean = false, language: String = "ru"): List<TmdbMovieDto>
    suspend fun discoverMediaByParams(params: Map<String, String>, targetType: EntityType, page: Int = 1, language: String = "ru"): List<TmdbMovieDto>
}
