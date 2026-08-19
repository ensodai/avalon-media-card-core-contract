package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UserMediaBinding(
    val sourceType: String,
    val sourceId: String
)

/**
 * Провайдер для работы с личными привязками медиа к источникам (например, конкретному торренту или CDN).
 */
interface UserMediaBindingProvider {
    /**
     * Возвращает сохранённый идентификатор источника для пользователя и медиа.
     * @param userId ID пользователя
     * @param mediaId ID фильма или сериала (Tmdb ID)
     * @param sourceType Тип источника (например, "torrserver")
     * @return идентификатор источника (например, хеш торрента) или null
     */
    suspend fun getBinding(userId: Uuid, mediaId: String, sourceType: String): String?

    /**
     * Возвращает любую активную привязку источника для пользователя и медиа.
     */
    suspend fun getActiveBinding(userId: Uuid, mediaId: String): UserMediaBinding? = null

    /**
     * Наблюдает за активной привязкой источника для пользователя и медиа.
     */
    fun observeActiveBinding(userId: Uuid, mediaId: String): Flow<UserMediaBinding?> = flowOf(null)

    /**
     * Сохраняет привязку источника для пользователя.
     * @param userId ID пользователя
     * @param mediaId ID фильма или сериала (Tmdb ID)
     * @param sourceType Тип источника (например, "torrserver")
     * @param sourceId Идентификатор источника (например, хеш торрента)
     */
    suspend fun saveBinding(userId: Uuid, mediaId: String, sourceType: String, sourceId: String)

    /**
     * Удаляет привязку источника для пользователя.
     * @param userId ID пользователя
     * @param mediaId ID фильма или сериала (Tmdb ID)
     * @param sourceType Тип источника (например, "torrserver")
     */
    suspend fun deleteBinding(userId: Uuid, mediaId: String, sourceType: String)

    /**
     * Удаляет все привязки для пользователя и медиа.
     */
    suspend fun deleteAllBindings(userId: Uuid, mediaId: String) {}
}
