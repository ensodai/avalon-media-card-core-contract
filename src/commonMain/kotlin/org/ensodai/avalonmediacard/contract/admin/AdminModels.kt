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
) {
    override fun toString(): String =
        "CreateUserRequest(username='$username', passwordRaw='***', role=$role, status=$status)"
}

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
) {
    override fun toString(): String =
        "GlobalIntegrationSettingsDto(tmdbReadToken=${if (tmdbReadToken != null) "***" else null}, tmdbShareWithUsers=$tmdbShareWithUsers, torrServerHost=$torrServerHost, torrServerLogin=$torrServerLogin, torrServerPassword=${if (torrServerPassword != null) "***" else null}, torrServerShareWithUsers=$torrServerShareWithUsers, torrServerUseGst=$torrServerUseGst, prowlarrUrl=$prowlarrUrl, prowlarrApiKey=${if (prowlarrApiKey != null) "***" else null}, prowlarrShareWithUsers=$prowlarrShareWithUsers, jackettUrl=$jackettUrl, jackettApiKey=${if (jackettApiKey != null) "***" else null}, jackettShareWithUsers=$jackettShareWithUsers)"
}

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
) {
    override fun toString(): String =
        "UpdateGlobalIntegrationSettingsRequest(tmdbReadToken=${if (tmdbReadToken != null) "***" else null}, tmdbShareWithUsers=$tmdbShareWithUsers, torrServerHost=$torrServerHost, torrServerLogin=$torrServerLogin, torrServerPassword=${if (torrServerPassword != null) "***" else null}, torrServerShareWithUsers=$torrServerShareWithUsers, torrServerUseGst=$torrServerUseGst, prowlarrUrl=$prowlarrUrl, prowlarrApiKey=${if (prowlarrApiKey != null) "***" else null}, prowlarrShareWithUsers=$prowlarrShareWithUsers, jackettUrl=$jackettUrl, jackettApiKey=${if (jackettApiKey != null) "***" else null}, jackettShareWithUsers=$jackettShareWithUsers)"
}

@Serializable
data class UpdateTmdbSettingsRequest(
    val token: String?,
    val shareWithUsers: Boolean = true
) {
    override fun toString(): String =
        "UpdateTmdbSettingsRequest(token=${if (token != null) "***" else null}, shareWithUsers=$shareWithUsers)"
}

@Serializable
data class UpdateTorrServerSettingsRequest(
    val host: String?,
    val login: String?,
    val password: String?,
    val shareWithUsers: Boolean = false,
    val useGst: Boolean = false
) {
    override fun toString(): String =
        "UpdateTorrServerSettingsRequest(host=$host, login=$login, password=${if (password != null) "***" else null}, shareWithUsers=$shareWithUsers, useGst=$useGst)"
}

@Serializable
data class UpdateProwlarrSettingsRequest(
    val url: String?,
    val apiKey: String?,
    val shareWithUsers: Boolean = false
) {
    override fun toString(): String =
        "UpdateProwlarrSettingsRequest(url=$url, apiKey=${if (apiKey != null) "***" else null}, shareWithUsers=$shareWithUsers)"
}

@Serializable
data class UpdateJackettSettingsRequest(
    val url: String?,
    val apiKey: String?,
    val shareWithUsers: Boolean = false
) {
    override fun toString(): String =
        "UpdateJackettSettingsRequest(url=$url, apiKey=${if (apiKey != null) "***" else null}, shareWithUsers=$shareWithUsers)"
}

@Serializable
data class ServerSystemInfoDto(
    val coreVersion: String,
    val protocolVersion: String,
    val buildDate: String,
    val uptimeSeconds: Long,
    val databaseType: String,
    val activeUsersCount: Long,
    val totalUsersCount: Long,
    val cachedMediaCount: Long,
    val cachedDiscoverQueriesCount: Long,
    val cachedUserFeedsCount: Long,
    val loadedPluginsCount: Int,
    val javaVersion: String,
    val osName: String
)
