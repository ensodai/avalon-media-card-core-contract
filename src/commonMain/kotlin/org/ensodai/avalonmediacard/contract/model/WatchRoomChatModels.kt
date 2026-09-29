package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Модель текстового сообщения чата в комнате совместного просмотра.
 */
@Serializable
data class WatchRoomChatMessageDto(
    val id: Uuid,
    val roomId: Uuid,
    val senderUserId: Uuid,
    val senderUsername: String,
    val senderAvatarUrl: String? = null,
    val text: String,
    val playbackPositionMs: Long,
    val season: Int? = null,
    val episode: Int? = null,
    val createdAt: Instant
)
