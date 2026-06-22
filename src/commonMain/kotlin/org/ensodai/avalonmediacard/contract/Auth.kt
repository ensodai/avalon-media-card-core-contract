package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val error: String? = null
)

@Serializable
data class UserInfo(
    val id: String,
    val username: String,
    val role: String
)

@Serializable
sealed class AuthState {
    @Serializable
    object Guest : AuthState()

    @Serializable
    data class Authorized(
        val userId: String,
        val username: String,
        val role: String
    ) : AuthState()
}
