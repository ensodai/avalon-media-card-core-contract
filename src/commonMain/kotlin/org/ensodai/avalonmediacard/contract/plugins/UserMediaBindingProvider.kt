package org.ensodai.avalonmediacard.contract.plugins

import kotlin.uuid.Uuid

/**
 * Провайдер для работы с личными привязками медиа к источникам (например, конкретному торренту или CDN).
 */
interface UserMediaBindingProvider {
    /**
     * Возвращает сохранённый идентификатор источника для пользователя и медиа.
     * @param userId ID пользователя
     * @param mediaId ID фильма или сериала (Tmdb ID)
     * @param sourceType Тип источника (например, "TORRENT")
     * @return идентификатор источника (например, хеш торрента) или null
     */
    suspend fun getBinding(userId: Uuid, mediaId: String, sourceType: String): String?

    /**
     * Сохраняет привязку источника для пользователя.
     * @param userId ID пользователя
     * @param mediaId ID фильма или сериала (Tmdb ID)
     * @param sourceType Тип источника (например, "TORRENT")
     * @param sourceId Идентификатор источника (например, хеш торрента)
     */
    suspend fun saveBinding(userId: Uuid, mediaId: String, sourceType: String, sourceId: String)

    /**
     * Удаляет привязку источника для пользователя.
     * @param userId ID пользователя
     * @param mediaId ID фильма или сериала (Tmdb ID)
     * @param sourceType Тип источника (например, "TORRENT")
     */
    suspend fun deleteBinding(userId: Uuid, mediaId: String, sourceType: String)
}
