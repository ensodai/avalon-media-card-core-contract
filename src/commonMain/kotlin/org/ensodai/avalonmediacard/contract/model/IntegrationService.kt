package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
enum class IntegrationService {
    TRAKT, MYSHOWS;

    val id: String
        get() = name.lowercase()

    companion object {
        fun fromId(id: String): IntegrationService? {
            return entries.find { it.id == id.lowercase() }
        }
    }
}
