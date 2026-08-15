package org.ensodai.avalonmediacard.contract.rpc

import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.admin.AdminActionResponse
import org.ensodai.avalonmediacard.contract.admin.CreateUserRequest
import org.ensodai.avalonmediacard.contract.admin.GlobalIntegrationSettingsDto
import org.ensodai.avalonmediacard.contract.admin.ServerSystemInfoDto
import org.ensodai.avalonmediacard.contract.admin.UpdateGlobalIntegrationSettingsRequest
import org.ensodai.avalonmediacard.contract.admin.UserDto
import org.ensodai.avalonmediacard.contract.model.UserRole
import org.ensodai.avalonmediacard.contract.model.UserStatus

@Rpc
interface AdminRpcService {
    suspend fun uploadPlugin(fileName: String, fileContent: ByteArray): Boolean
    suspend fun createUser(request: CreateUserRequest): AdminActionResponse
    suspend fun getUsers(): List<UserDto>
    suspend fun updateUserStatus(userId: String, status: UserStatus): AdminActionResponse
    suspend fun updateUserRole(userId: String, role: UserRole): AdminActionResponse
    suspend fun resetUserPassword(userId: String, newPasswordRaw: String): AdminActionResponse
    suspend fun deleteUser(userId: String): AdminActionResponse
    
    suspend fun getGlobalIntegrationSettings(): GlobalIntegrationSettingsDto
    suspend fun updateGlobalIntegrationSettings(request: UpdateGlobalIntegrationSettingsRequest): AdminActionResponse
    suspend fun testTmdbConnection(token: String): AdminActionResponse
    suspend fun testTorrServerConnection(host: String, login: String?, password: String?): AdminActionResponse
    suspend fun testProwlarrConnection(url: String, apiKey: String): AdminActionResponse
    suspend fun testJackettConnection(url: String, apiKey: String): AdminActionResponse

    suspend fun getSystemInfo(): ServerSystemInfoDto
    suspend fun clearDiscoverCache(): AdminActionResponse
    suspend fun clearFeedCache(): AdminActionResponse
    suspend fun clearMediaCache(): AdminActionResponse
}
