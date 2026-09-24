package org.ensodai.avalonmediacard.contract.rpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.model.CreateRoomRequest
import org.ensodai.avalonmediacard.contract.model.JoinRoomResult
import org.ensodai.avalonmediacard.contract.model.LobbyEvent
import org.ensodai.avalonmediacard.contract.model.RoomPlaybackCommand
import org.ensodai.avalonmediacard.contract.model.SetLobbyStatusRequest
import org.ensodai.avalonmediacard.contract.model.WatchRoomDto
import org.ensodai.avalonmediacard.contract.model.WatchRoomEvent
import org.ensodai.avalonmediacard.contract.model.WatchRoomSummaryDto
import kotlin.uuid.Uuid

@Rpc
interface WatchPartyRpcService {
    /**
     * Создает новую комнату совместного просмотра.
     */
    suspend fun createRoom(request: CreateRoomRequest): WatchRoomDto

    /**
     * Вход в комнату по временному PIN-коду (быстрый ввод с ТВ).
     */
    suspend fun joinRoomByPin(pin: String): JoinRoomResult

    /**
     * Вход в комнату по постоянному UUID (по ссылке или из списка сохраненных комнат).
     */
    suspend fun joinRoomById(roomId: Uuid): JoinRoomResult

    /**
     * Возвращает список сохраненных активных комнат для конкретного тайтла.
     */
    suspend fun getSavedRoomsForMedia(mediaId: String): List<WatchRoomSummaryDto>

    /**
     * Возвращает список всех комнат пользователя (где он хост или участник).
     */
    suspend fun getUserRooms(): List<WatchRoomSummaryDto>

    /**
     * Покинуть комнату.
     */
    suspend fun leaveRoom(roomId: Uuid): Boolean

    /**
     * Закрыть комнату (только для хоста).
     */
    suspend fun closeRoom(roomId: Uuid): Boolean

    /**
     * Реактивный поток событий предстартового лобби (мультиплексируется поверх единого WebSocket-канала).
     */
    fun streamLobbyState(roomId: Uuid): Flow<LobbyEvent>

    /**
     * Обновление готовности и намерения участника в лобби.
     */
    suspend fun setLobbyStatus(roomId: Uuid, request: SetLobbyStatusRequest): Boolean

    /**
     * Команда хоста на запуск совместного просмотра из лобби.
     */
    suspend fun triggerStartPlayback(roomId: Uuid): Boolean

    /**
     * Реактивный поток событий комнаты в реальном времени (WebSocket).
     */
    fun streamRoomEvents(roomId: Uuid): Flow<WatchRoomEvent>

    /**
     * Отправка команды управления воспроизведением (Play, Pause, Seek, ChangeEpisode).
     */
    suspend fun sendPlaybackCommand(roomId: Uuid, command: RoomPlaybackCommand): Boolean

    /**
     * Отправка быстрой эмодзи-реакции поверх экрана.
     */
    suspend fun sendReaction(roomId: Uuid, emoji: String): Boolean
}
