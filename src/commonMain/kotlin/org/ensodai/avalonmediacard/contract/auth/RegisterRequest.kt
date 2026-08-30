package org.ensodai.avalonmediacard.contract.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String
) {
    override fun toString(): String = "RegisterRequest(username='$username', password='***')"
}
