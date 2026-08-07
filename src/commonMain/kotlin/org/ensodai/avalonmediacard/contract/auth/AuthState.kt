package org.ensodai.avalonmediacard.contract.auth

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.model.UserRole
import kotlin.uuid.Uuid

@Serializable
sealed class AuthState {
    @Serializable
    object Guest : AuthState()

    @Serializable
    data class Authorized(
        val userId: Uuid,
        val username: String,
        val role: UserRole
    ) : AuthState()
}
