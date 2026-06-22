package org.ensodai.avalonmediacard.contract

import kotlinx.rpc.annotations.Rpc

@Rpc
interface AuthRpcService {
    suspend fun login(request: LoginRequest): AuthResponse
    suspend fun register(request: RegisterRequest): AuthResponse
    suspend fun authenticate(token: String): AuthResponse?
}
