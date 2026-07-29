package org.ensodai.avalonmediacard.contract.rpc

import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.model.IntegrationStatus
import org.ensodai.avalonmediacard.contract.auth.LoginRequest
import org.ensodai.avalonmediacard.contract.auth.RegisterRequest
import org.ensodai.avalonmediacard.contract.auth.AuthResponse

@Rpc
interface AuthRpcService {
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun register(request: RegisterRequest): AuthResponse
    suspend fun authenticate(token: String): AuthResponse?

    suspend fun getOAuthUrl(service: String): String
    suspend fun exchangeOAuthCode(service: String, code: String): Boolean
    suspend fun getIntegrationsStatus(): List<IntegrationStatus>
}
