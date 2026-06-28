package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
enum class MediaStatus {
    NONE,       // Нет статуса
    WATCHING,   // Смотрю
    PLANNED,    // В планах
    COMPLETED,  // Просмотрено
    DROPPED     // Бросил
}
