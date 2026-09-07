package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.model.MediaKey
import org.ensodai.avalonmediacard.contract.model.NotificationType
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
data class EpisodeNotificationItemDto(
    val id: Uuid,
    val userId: Uuid,
    val mediaKey: MediaKey,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val airDate: String? = null,
    val isRead: Boolean = false,
    val isDismissed: Boolean = false,
    val createdAt: Instant,
    val notificationType: NotificationType = NotificationType.EPISODE_RELEASE
)

interface UserEpisodeNotificationProvider {
    fun observeUnreadCount(userId: Uuid): Flow<Int>
    suspend fun getNotifications(userId: Uuid, limit: Int = 100): List<EpisodeNotificationItemDto>
    suspend fun markAllAsRead(userId: Uuid)
    suspend fun markAsRead(userId: Uuid, notificationId: Uuid)
    suspend fun dismiss(userId: Uuid, notificationId: Uuid)
    suspend fun syncNotifications(userId: Uuid)
    suspend fun markWatched(
        userId: Uuid,
        catalogId: String,
        externalId: String,
        seasonNumber: Int,
        episodeNumber: Int
    )
    suspend fun markMovieWatched(
        userId: Uuid,
        catalogId: String,
        externalId: String
    )
}

object DummyUserEpisodeNotificationProvider : UserEpisodeNotificationProvider {
    override fun observeUnreadCount(userId: Uuid): Flow<Int> = emptyFlow()
    override suspend fun getNotifications(userId: Uuid, limit: Int): List<EpisodeNotificationItemDto> = emptyList()
    override suspend fun markAllAsRead(userId: Uuid) {}
    override suspend fun markAsRead(userId: Uuid, notificationId: Uuid) {}
    override suspend fun dismiss(userId: Uuid, notificationId: Uuid) {}
    override suspend fun syncNotifications(userId: Uuid) {}
    override suspend fun markWatched(userId: Uuid, catalogId: String, externalId: String, seasonNumber: Int, episodeNumber: Int) {}
    override suspend fun markMovieWatched(userId: Uuid, catalogId: String, externalId: String) {}
}
