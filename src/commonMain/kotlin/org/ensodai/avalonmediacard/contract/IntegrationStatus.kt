package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
data class IntegrationStatus(
    val service: String,
    val connected: Boolean,
    val username: String? = null
)
