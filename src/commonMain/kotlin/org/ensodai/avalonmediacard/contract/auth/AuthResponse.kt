package org.ensodai.avalonmediacard.contract.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val error: String? = null
)
