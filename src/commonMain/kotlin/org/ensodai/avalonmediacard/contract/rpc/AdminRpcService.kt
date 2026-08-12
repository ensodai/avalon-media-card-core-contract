package org.ensodai.avalonmediacard.contract.rpc

import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.admin.AdminActionResponse
import org.ensodai.avalonmediacard.contract.admin.CreateUserRequest
import org.ensodai.avalonmediacard.contract.admin.UserDto
import org.ensodai.avalonmediacard.contract.model.UserStatus

@Rpc
interface AdminRpcService {
    suspend fun uploadPlugin(fileName: String, fileContent: ByteArray): Boolean
    suspend fun createUser(request: CreateUserRequest): AdminActionResponse
    suspend fun getUsers(): List<UserDto>
    suspend fun updateUserStatus(userId: String, status: UserStatus): AdminActionResponse
    suspend fun updateUserRole(userId: String, role: org.ensodai.avalonmediacard.contract.model.UserRole): AdminActionResponse
    suspend fun resetUserPassword(userId: String, newPasswordRaw: String): AdminActionResponse
    suspend fun deleteUser(userId: String): AdminActionResponse
    
    suspend fun getGlobalIntegrationSettings(): org.ensodai.avalonmediacard.contract.admin.GlobalIntegrationSettingsDto
    suspend fun updateGlobalIntegrationSettings(request: org.ensodai.avalonmediacard.contract.admin.UpdateGlobalIntegrationSettingsRequest): AdminActionResponse
}
