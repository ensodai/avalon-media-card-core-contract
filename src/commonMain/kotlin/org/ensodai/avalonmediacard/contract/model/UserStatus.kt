package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserStatus {
    ACTIVE,
    FROZEN
}
