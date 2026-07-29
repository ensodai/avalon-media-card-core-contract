package org.ensodai.avalonmediacard.contract.auth

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
sealed class AuthState {
    @Serializable
    object Guest : AuthState()

    @Serializable
    data class Authorized(
        val userId: Uuid,
        val username: String,
        val role: String
    ) : AuthState()
}
