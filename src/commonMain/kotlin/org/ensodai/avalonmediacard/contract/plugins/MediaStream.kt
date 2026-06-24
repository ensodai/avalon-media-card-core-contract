package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.serialization.Serializable

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
    val sizeBytes: Long? = null,
    val sourceName: String,
    val seeders: Int? = null,     // Применимо для торрентов
    val leechers: Int? = null     // Применимо для торрентов
)
