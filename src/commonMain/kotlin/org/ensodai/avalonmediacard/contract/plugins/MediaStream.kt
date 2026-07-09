package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.slot.Action

/**
 * Тип видеопотока.
 */
@Serializable
enum class StreamType {
    DirectUrl,  // Прямая ссылка на файл (mp4, mkv и др.)
    Hls,        // Стриминг HLS (.m3u8)
    Torrent,    // Ссылка на .torrent файл
    Magnet      // Magnet-ссылка
}

/**
 * Результат поиска видеопотока.
 */
@Serializable
data class MediaStream(
    val title: String,
    val url: String,
    val type: StreamType,
    val quality: String? = null,
    val format: String? = null,
    val videoCodec: String? = null,
    val audioCodec: String? = null,
    val isHdr: Boolean = false,
    val sizeBytes: Long? = null,
    val durationSeconds: Double? = null,
    val sourceName: String,
    val seeders: Int? = null,     // Применимо для торрентов
    val leechers: Int? = null,     // Применимо для торрентов
    val clickAction: Action? = null,
    // Поля для смапленных эпизодов
    val isMapped: Boolean = false,
    val episodeName: String? = null,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val episodePosterUrl: String? = null,
    // Поля для прогресса просмотра
    val watchedProgressSeconds: Long? = null,
    val isWatched: Boolean = false,
    val userRating: Int? = null
)
