package org.ensodai.avalonmediacard.contract.rpc

import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.model.MediaKey
import org.ensodai.avalonmediacard.contract.plugins.AudioTrack
import org.ensodai.avalonmediacard.contract.plugins.MediaStream
import org.ensodai.avalonmediacard.contract.plugins.SubtitleTrack
import org.ensodai.avalonmediacard.contract.slot.TorrentFileItem

@Rpc
interface PlaybackRpcService {
    /**
     * Фаза 1 (Ядро): Мгновенно возвращает структуру UI плеера
     * (плейлист сезона, названия из TMDB, тайминг и прогресс из БД).
     */
    suspend fun getPlaybackMetadata(
        key: MediaKey,
        seasonNumber: Int? = null,
        episodeNumber: Int? = null
    ): PlaybackMetadataResult

    /**
     * Фаза 2 (Плагины): Готовит прямой CDN-поток для конкретной серии.
     */
    suspend fun getStreamUrl(
        key: MediaKey,
        seasonNumber: Int? = null,
        episodeNumber: Int? = null
    ): StreamPlaybackResult

    suspend fun selectSource(
        key: MediaKey,
        providerId: String,
        sourceId: String,
        seasonNumber: Int? = null,
        episodeNumber: Int? = null
    ): SourceSelectionResult

    suspend fun searchSources(
        key: MediaKey,
        forceRefresh: Boolean = false
    ): Boolean
}

@Serializable
sealed interface PlaybackMetadataResult {
    @Serializable
    data class Ready(
        val currentSeason: Int?,
        val currentEpisode: Int?,
        val episodeTitle: String,
        val seriesTitle: String? = null,
        val durationSeconds: Double? = null,
        val startPositionSeconds: Long? = null,
        val playlist: List<MediaStream> = emptyList(),
        val boundSourceTitle: String? = null
    ) : PlaybackMetadataResult

    @Serializable
    data object NoSourceBound : PlaybackMetadataResult

    @Serializable
    data class Error(
        val message: String
    ) : PlaybackMetadataResult
}


@Serializable
sealed interface SourceSelectionResult {
    @Serializable
    data class Ready(
        val targetSeason: Int? = null,
        val targetEpisode: Int? = null
    ) : SourceSelectionResult

    @Serializable
    data class RequiresManualMapping(
        val torrentHash: String,
        val torrentTitle: String,
        val files: List<TorrentFileItem>
    ) : SourceSelectionResult

    @Serializable
    data class Error(
        val message: String
    ) : SourceSelectionResult
}

@Serializable
sealed interface StreamPlaybackResult {
    @Serializable
    data class Ready(
        val streamUrl: String,
        val streamId: String,
        val durationSeconds: Double?,
        val startPositionSeconds: Long? = null,
        val audioTracks: List<AudioTrack> = emptyList(),
        val subtitleTracks: List<SubtitleTrack> = emptyList(),
        val audioTrackIndex: Int? = null,
        val playlist: List<MediaStream> = emptyList()
    ) : StreamPlaybackResult

    @Serializable
    data class NoSourceBound(
        val message: String = "Источник не выбран"
    ) : StreamPlaybackResult

    @Serializable
    data class Error(
        val message: String
    ) : StreamPlaybackResult
}

