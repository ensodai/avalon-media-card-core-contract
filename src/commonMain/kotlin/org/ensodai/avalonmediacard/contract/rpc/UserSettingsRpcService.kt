package org.ensodai.avalonmediacard.contract.rpc

import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.model.UserSettingsDto

@Rpc
interface UserSettingsRpcService {
    suspend fun getUserSettings(): UserSettingsDto
    suspend fun updateUserSettings(settings: UserSettingsDto): Boolean
}
