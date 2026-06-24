package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UserInfo(
    val id: Uuid,
    val username: String,
    val role: String
)
