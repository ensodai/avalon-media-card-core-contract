package org.ensodai.avalonmediacard.contract.auth

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.model.UserRole

@Serializable
data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val error: String? = null,
    val role: UserRole? = null,
    val userId: String? = null,
    val username: String? = null
)

