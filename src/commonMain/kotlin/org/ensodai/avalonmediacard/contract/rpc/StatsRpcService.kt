package org.ensodai.avalonmediacard.contract.rpc

import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.model.DayActivityDto
import org.ensodai.avalonmediacard.contract.model.UserStatsDto

/**
 * RPC-сервис для получения аналитики и персональной статистики пользователя.
 */
@Rpc
interface StatsRpcService {
    suspend fun getUserStats(): UserStatsDto
    suspend fun getDayActivity(date: String): DayActivityDto
}
