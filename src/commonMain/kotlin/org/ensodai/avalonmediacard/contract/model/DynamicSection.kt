package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.EntityType
import org.ensodai.avalonmediacard.contract.MediaKey

@Serializable
enum class SectionType {
    CAROUSEL_POSTERS,   // Стандартная карусель вертикальных постеров
    CAROUSEL_BACKDROPS, // Карусель широких горизонтальных кадров
    CAROUSEL_AVATARS,   // Карусель круглых аватарок (актеры/режиссеры)
    EXPLORATION,        // Специальный UI-блок для серендипности
    HERO                // Большой акцентный баннер
}

@Serializable
data class DynamicSection(
    val id: String,
    val title: String,
    val type: SectionType,
    val targetType: EntityType = EntityType.MOVIE, // Указываем, фильм это или сериал
    val weight: Double, // То самое поле для сортировки плагином
    val queryParams: Map<String, String> = emptyMap(), // Параметры для TMDB Discover (если грузит клиент)
    val mediaIds: List<MediaKey> = emptyList(),        // Либо готовый список ID (если собрал бэкенд)
    val description: String? = null
)
