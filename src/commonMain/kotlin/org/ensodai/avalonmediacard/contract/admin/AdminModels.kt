package org.ensodai.avalonmediacard.contract.admin

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.model.UserRole

@Serializable
data class CreateUserRequest(
    val username: String,
    val passwordRaw: String,
    val role: UserRole = UserRole.USER
)

@Serializable
data class AdminActionResponse(
    val success: Boolean,
    val error: String? = null
)
