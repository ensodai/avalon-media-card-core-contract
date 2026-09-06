package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable

@Serializable
enum class NotificationType {
    EPISODE_RELEASE,
    MOVIE_RELEASE
}
