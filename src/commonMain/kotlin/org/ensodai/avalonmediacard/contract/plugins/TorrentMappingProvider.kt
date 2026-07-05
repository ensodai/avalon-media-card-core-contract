package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.serialization.Serializable

@Serializable
data class TorrentMapping(
    val id: String,
    val torrentHash: String,
    val filePath: String,
    val seasons: List<Int>?,
    val episodes: List<Int>?,
    val isAbsolute: Boolean,
    val isManual: Boolean,
    val mediaId: String? = null,
    val fileIndex: Int? = null,
    val fileSize: Long? = null
)

interface TorrentMappingProvider {
    /**
     * Получить все маппинги для конкретного торрента (по хэшу)
     */
    suspend fun getMappingsByHash(torrentHash: String): List<TorrentMapping>

    /**
     * Получить все маппинги для конкретного mediaId
     */
    suspend fun getMappingsByMediaId(mediaId: String): List<TorrentMapping>

    /**
     * Сохранить новый маппинг или обновить существующий (upsert)
     */
    suspend fun saveMapping(
        torrentHash: String,
        filePath: String,
        seasons: List<Int>?,
        episodes: List<Int>?,
        isAbsolute: Boolean,
        isManual: Boolean,
        mediaId: String? = null,
        fileIndex: Int? = null,
        fileSize: Long? = null
    ): TorrentMapping
    
    /**
     * Удалить все маппинги для конкретного mediaId
     */
    suspend fun clearMappingsByMediaId(mediaId: String)
}
