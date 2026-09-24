package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
enum class WatchRoomStatus {
    ACTIVE,
    PAUSED,
    ARCHIVED
}

@Serializable
enum class WatchRoomControlMode {
    HOST_ONLY,
    DEMOCRATIC
}

@Serializable
enum class WatchRoomParticipantRole {
    HOST,
    MEMBER
}

@Serializable
enum class WatchRoomPlaybackState {
    READY,
    PLAYING,
    PAUSED,
    BUFFERING,
    OFFLINE
}

@Serializable
enum class WatchParticipantIntent {
    WATCHING_ATTENTIVELY, // 🍿 Смотрю с интересом
    BACKGROUND_LISTENING, // 🎧 Слушаю на фоне
    AWAY_FOR_SNACKS,      // ☕ Отошел за чаем / перекусом
    SILENT_NO_PAUSES,     // 🤫 Без пауз и спойлеров
    ACTIVE_DISCUSSION,    // 💬 Буду комментировать
    CHILLING              // 🛋️ Просто отдыхаю
}

@Serializable
data class WatchRoomParticipantDto(
    val userId: Uuid,
    val username: String,
    val role: WatchRoomParticipantRole,
    val isOnline: Boolean = true,
    val playbackState: WatchRoomPlaybackState = WatchRoomPlaybackState.READY,
    val isReady: Boolean = false,
    val intent: WatchParticipantIntent = WatchParticipantIntent.WATCHING_ATTENTIVELY
)

@Serializable
data class WatchRoomDto(
    val id: Uuid,
    val title: String,
    val mediaId: String,
    val mediaType: MediaType,
    val currentSeason: Int? = null,
    val currentEpisode: Int? = null,
    val lastPositionSeconds: Long = 0L,
    val hostUserId: Uuid,
    val sourceType: String? = null,
    val sourceId: String? = null,
    val joinPin: String? = null,
    val controlMode: WatchRoomControlMode = WatchRoomControlMode.HOST_ONLY,
    val status: WatchRoomStatus = WatchRoomStatus.ACTIVE,
    val isPrivate: Boolean = false,
    val participants: List<WatchRoomParticipantDto> = emptyList()
)

@Serializable
data class WatchRoomSummaryDto(
    val id: Uuid,
    val title: String,
    val mediaId: String,
    val mediaType: MediaType,
    val currentSeason: Int? = null,
    val currentEpisode: Int? = null,
    val lastPositionSeconds: Long = 0L,
    val joinPin: String? = null,
    val participantsCount: Int = 1,
    val isHost: Boolean = false,
    val status: WatchRoomStatus = WatchRoomStatus.ACTIVE
)

@Serializable
data class CreateRoomRequest(
    val mediaId: String,
    val mediaType: MediaType,
    val title: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val startPositionSeconds: Long = 0L,
    val sourceType: String? = null,
    val sourceId: String? = null,
    val controlMode: WatchRoomControlMode = WatchRoomControlMode.HOST_ONLY,
    val isPrivate: Boolean = false
)

@Serializable
sealed interface JoinRoomResult {
    @Serializable
    data class Success(val room: WatchRoomDto) : JoinRoomResult

    @Serializable
    data class Error(val message: String) : JoinRoomResult
}

@Serializable
sealed interface RoomPlaybackCommand {
    @Serializable
    data class Play(val positionMs: Long) : RoomPlaybackCommand

    @Serializable
    data class Pause(val positionMs: Long) : RoomPlaybackCommand

    @Serializable
    data class Seek(val targetPositionMs: Long) : RoomPlaybackCommand

    @Serializable
    data class ChangeEpisode(val season: Int, val episode: Int) : RoomPlaybackCommand

    @Serializable
    data class ReportBuffer(val isBuffering: Boolean, val bufferPercent: Int = 100) : RoomPlaybackCommand

    @Serializable
    data class SetLobbyStatus(val intent: WatchParticipantIntent, val isReady: Boolean) : RoomPlaybackCommand
}

@Serializable
sealed interface WatchRoomEvent {
    @Serializable
    data class SyncState(
        val isPlaying: Boolean,
        val anchorPositionMs: Long,
        val anchorServerTimestampMs: Long,
        val season: Int? = null,
        val episode: Int? = null,
        val triggeredByUserId: Uuid? = null
    ) : WatchRoomEvent

    @Serializable
    data class ParticipantsUpdated(
        val participants: List<WatchRoomParticipantDto>
    ) : WatchRoomEvent

    @Serializable
    data class ReactionTriggered(
        val userId: Uuid,
        val username: String,
        val emoji: String
    ) : WatchRoomEvent

    @Serializable
    data class SystemNotice(
        val message: String
    ) : WatchRoomEvent
}

@Serializable
data class SetLobbyStatusRequest(
    val isReady: Boolean,
    val intent: WatchParticipantIntent
)

@Serializable
sealed interface LobbyEvent {
    @Serializable
    data class InitialSnapshot(
        val roomStatus: WatchRoomStatus,
        val participants: List<WatchRoomParticipantDto>
    ) : LobbyEvent

    @Serializable
    data class ParticipantUpdated(
        val participant: WatchRoomParticipantDto
    ) : LobbyEvent

    @Serializable
    data class ParticipantRemoved(
        val userId: Uuid
    ) : LobbyEvent

    @Serializable
    data class TransitionToPlayer(
        val playAtServerTimestampMs: Long,
        val season: Int?,
        val episode: Int?
    ) : LobbyEvent

    @Serializable
    data class SystemNotice(
        val message: String
    ) : LobbyEvent
}
