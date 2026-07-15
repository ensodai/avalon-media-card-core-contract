package org.ensodai.avalonmediacard.contract.plugins

import org.ensodai.avalonmediacard.contract.model.TelemetryEvent
import kotlin.uuid.Uuid

/**
 * Интерфейс, который предоставляет плагинам доступ к истории телеметрии пользователя.
 */
interface TelemetryProvider {
    /**
     * Возвращает список событий телеметрии для пользователя, отсортированных по дате (новые первыми).
     */
    suspend fun getUserEvents(userId: Uuid, limit: Int = 1000): List<TelemetryEvent>
}
