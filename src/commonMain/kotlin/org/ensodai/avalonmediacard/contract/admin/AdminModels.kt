package org.ensodai.avalonmediacard.contract.admin

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.model.UserRole
import org.ensodai.avalonmediacard.contract.model.UserStatus

@Serializable
data class CreateUserRequest(
    val username: String,
    val passwordRaw: String,
    val role: UserRole = UserRole.USER,
    val status: UserStatus = UserStatus.ACTIVE
)

@Serializable
data class AdminActionResponse(
    val success: Boolean,
    val error: String? = null
)

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val role: UserRole,
    val status: UserStatus
)

@Serializable
data class GlobalIntegrationSettingsDto(
    val tmdbReadToken: String?,
    val tmdbShareWithUsers: Boolean = true,
    val torrServerHost: String?,
    val torrServerLogin: String?,
    val torrServerPassword: String?,
    val torrServerShareWithUsers: Boolean = false,
    val torrServerUseGst: Boolean = false,
    val prowlarrUrl: String? = null,
    val prowlarrApiKey: String? = null,
    val prowlarrShareWithUsers: Boolean = false,
    val jackettUrl: String? = null,
    val jackettApiKey: String? = null,
    val jackettShareWithUsers: Boolean = false
)

@Serializable
data class UpdateGlobalIntegrationSettingsRequest(
    val tmdbReadToken: String?,
    val tmdbShareWithUsers: Boolean? = null,
    val torrServerHost: String?,
    val torrServerLogin: String?,
    val torrServerPassword: String?,
    val torrServerShareWithUsers: Boolean? = null,
    val torrServerUseGst: Boolean? = null,
    val prowlarrUrl: String? = null,
    val prowlarrApiKey: String? = null,
    val prowlarrShareWithUsers: Boolean? = null,
    val jackettUrl: String? = null,
    val jackettApiKey: String? = null,
    val jackettShareWithUsers: Boolean? = null
)
