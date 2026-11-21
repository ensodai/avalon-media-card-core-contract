package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable

/**
 * Главный агрегат экрана статистики.
 * Не содержит значений по умолчанию по соглашению проекта.
 */
@Serializable
data class UserStatsDto(
    val timeOverview: TimeStatsDto,
    val libraryOverview: LibraryStatsDto,
    val genreBreakdown: List<GenreStatDto>,
    val ratingDistribution: RatingDistributionDto,
    val topPeople: TopPeopleStatsDto,
    val archetype: ArchetypeDto?,
    val decadesBreakdown: List<DecadeStatDto>,
    val activityHeatmap: ActivityHeatmapDto
)

/**
 * Звание / веха экранного времени зрителя (присваивается на сервере).
 */
@Serializable
enum class TimeMilestoneType {
    FIRST_STEPS,
    LOTR_MARATHON,
    APOLLO_11,
    ORBITAL_MISSION,
    ARCHIVIST
}

/**
 * Объект вехи времени с параметром (например, множитель марафона или количество суток).
 */
@Serializable
data class TimeMilestoneDto(
    val type: TimeMilestoneType,
    val param: Int
)

/**
 * Экранное время в минутах и присвоенная сервером веха.
 */
@Serializable
data class TimeStatsDto(
    val totalMinutes: Long,
    val moviesMinutes: Long,
    val showsMinutes: Long,
    val milestone: TimeMilestoneDto
)

/**
 * Объем библиотеки и состояние бэклога.
 */
@Serializable
data class LibraryStatsDto(
    val totalMoviesCount: Int,
    val totalEpisodesCount: Int,
    val totalShowsCount: Int,
    val watchingShowsCount: Int,
    val completedShowsCount: Int,
    val onHoldShowsCount: Int,
    val dnfShowsCount: Int,
    val watchingMoviesCount: Int,
    val completedMoviesCount: Int,
    val onHoldMoviesCount: Int,
    val dnfMoviesCount: Int
)

/**
 * Статистика по конкретному жанру.
 */
@Serializable
data class GenreStatDto(
    val genreId: Int,
    val genreName: String,
    val watchedCount: Int,
    val percentage: Float
)

/**
 * Статус строгости критика (вычисляется на сервере).
 */
@Serializable
enum class CriticProfileType {
    NO_RATINGS,
    ENTHUSIAST,
    GENEROUS,
    BALANCED,
    STRICT
}

/**
 * Распределение оценок (1..10), средний балл и статус критика.
 */
@Serializable
data class RatingDistributionDto(
    val averageRating: Double?,
    val totalRatedCount: Int,
    val distribution: Map<Int, Int>,
    val criticProfile: CriticProfileType
)

/**
 * Топ участников съемочного процесса (режиссеры и актеры).
 */
@Serializable
data class TopPeopleStatsDto(
    val directors: List<PersonStatDto>,
    val actors: List<PersonStatDto>
)

/**
 * Элемент лидерборда персоны.
 */
@Serializable
data class PersonStatDto(
    val personId: String,
    val name: String,
    val photoUrl: String?,
    val watchedCount: Int,
    val averageRating: Double?
)

/**
 * Тип кинематографического архетипа личности.
 */
@Serializable
enum class ArchetypeType {
    SCIFI_VISIONARY,
    SUSPENSE_HUNTER,
    DEEP_EMPATH,
    DOPAMINE_SEEKER,
    CURIOUS_VOYAGER
}

/**
 * Кинематографический архетип личности (чистые данные).
 */
@Serializable
data class ArchetypeDto(
    val type: ArchetypeType,
    val dominantGenreIds: List<Int>
)

/**
 * Распределение просмотров по десятилетиям.
 * startYear: 0 (до 1970), 1970, 1980, 1990, 2000, 2010, 2020.
 */
@Serializable
data class DecadeStatDto(
    val startYear: Int,
    val watchedCount: Int
)

/**
 * Данные активности за один календарный день для тепловой карты.
 */
@Serializable
data class HeatmapDayDto(
    val date: String,
    val minutes: Long,
    val count: Int,
    val avgRating: Double?
)

/**
 * Годовая тепловая карта активности синефила (52 недели).
 */
@Serializable
data class ActivityHeatmapDto(
    val days: List<HeatmapDayDto>,
    val totalActiveDays: Int,
    val currentStreakDays: Int,
    val longestStreakDays: Int,
    val peakDayDate: String?,
    val peakDayMinutes: Long
)

