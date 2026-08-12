package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UserInfo(
    val id: Uuid,
    val username: String,
    val role: UserRole,
    val status: UserStatus = UserStatus.ACTIVE
)
