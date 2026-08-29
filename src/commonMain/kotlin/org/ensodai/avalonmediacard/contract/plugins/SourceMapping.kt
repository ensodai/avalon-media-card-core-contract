package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.serialization.Serializable

@Serializable
data class SourceMapping(
    val id: String = "",
    val sourceType: String = "torrserver",
    val sourceId: String,
    val itemKey: String,
    val seasons: List<Int>? = null,
    val episodes: List<Int>? = null,
    val isAbsolute: Boolean = false,
    val isManual: Boolean = false,
    val mediaId: String? = null,
    val fileIndex: Int? = null,
    val fileSize: Long? = null,
    val streamUrl: String? = null,
    val quality: String? = null
)

interface SourceMappingProvider {
    /**
     * Получить все маппинги для конкретного источника (по sourceId: хэш торрента, ID релиза/сезона Рутуба/ВК/Анилибрии)
     */
    suspend fun getMappingsBySourceId(sourceId: String): List<SourceMapping>

    /**
     * Получить все маппинги для конкретного mediaId (TMDB ID)
     */
    suspend fun getMappingsByMediaId(mediaId: String): List<SourceMapping>

    /**
     * Получить все маппинги для конкретного mediaId и sourceId
     */
    suspend fun getMappings(mediaId: String, sourceId: String): List<SourceMapping>

    /**
     * Сохранить новый маппинг или обновить существующий (upsert)
     */
    suspend fun saveMapping(mapping: SourceMapping): SourceMapping

    /**
     * Пакетно сохранить или обновить список маппингов (upsert)
     */
    suspend fun saveMappingsBatch(mappings: List<SourceMapping>)

    /**
     * Удалить все маппинги для конкретного mediaId
     */
    suspend fun clearMappingsByMediaId(mediaId: String)

    /**
     * Удалить все маппинги для конкретного sourceId
     */
    suspend fun clearMappingsBySourceId(sourceId: String)
}

fun SourceMapping.toTorrentMapping(): TorrentMapping = TorrentMapping(
    id = id,
    torrentHash = sourceId,
    filePath = itemKey,
    seasons = seasons,
    episodes = episodes,
    isAbsolute = isAbsolute,
    isManual = isManual,
    mediaId = mediaId,
    fileIndex = fileIndex,
    fileSize = fileSize
)
