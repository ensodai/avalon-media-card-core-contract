package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.classification.AnimeSubType

@Serializable
data class ActorMetadata(
    val name: String,
    val originalName: String? = null,
    val character: String? = null,
    val profileUrl: String? = null,
    val id: String? = null
)

@Serializable
data class ProductionCompanyMetadata(
    val id: Int,
    val name: String,
    val logoUrl: String? = null
)

@Serializable
data class TrailerMetadata(
    val name: String,
    val videoUrl: String,
    val type: String? = null
)

@Serializable
data class RelatedMediaMetadata(
    val mediaId: String,
    val title: String,
    val posterUrl: String? = null
)

@Serializable
data class SeasonMetadata(
    val id: String,
    val seasonNumber: Int,
    val name: String,
    val overview: String? = null,
    val posterUrl: String? = null,
    val episodeCount: Int = 0,
    val airDate: String? = null
)

@Serializable
data class KeywordMetadata(
    val id: Int,
    val name: String,
    val documentFrequency: Int = 0
)

@Serializable
data class GenreMetadata(
    val id: Int,
    val name: String
)

@Serializable
data class MediaMetadata(
    val title: String,
    val originalTitle: String? = null,
    val imdbId: String? = null,
    val subtitle: String? = null,
    val description: String? = null,
    val posterUrl: String? = null,
    val backgroundUrl: String? = null,
    val rating: String? = null,
    val runtime: Int? = null,
    val genres: List<GenreMetadata> = emptyList(),
    val keywords: List<KeywordMetadata> = emptyList(),
    val productionCompanies: List<ProductionCompanyMetadata> = emptyList(),
    val releaseDate: String? = null,
    val tagline: String? = null,
    val director: String? = null,
    val directorImageUrl: String? = null,
    val directorId: String? = null,
    val cast: List<ActorMetadata> = emptyList(),
    val trailers: List<TrailerMetadata> = emptyList(),
    val recommendations: List<RelatedMediaMetadata> = emptyList(),
    val similar: List<RelatedMediaMetadata> = emptyList(),
    
    // TV Show specific metadata
    val numberOfSeasons: Int? = null,
    val numberOfEpisodes: Int? = null,
    val status: String? = null,
    val network: String? = null,
    val seasons: List<SeasonMetadata> = emptyList(),

    // Localized variants for user customization
    val localizedPosters: Map<String, String> = emptyMap(),
    val localizedOverviews: Map<String, String> = emptyMap(),

    // Anime Classification
    val animeSubType: AnimeSubType = AnimeSubType.NOT_ANIME
) {
    val isAnime: Boolean get() = animeSubType != AnimeSubType.NOT_ANIME
}

fun MediaMetadata.withUserSettings(settings: UserSettingsDto?): MediaMetadata {
    if (settings == null) return this

    val isOriginalTitle = settings.titleLanguage == "original" || settings.titleMode == TitleDisplayMode.ORIGINAL
    val effectiveTitle = if (isOriginalTitle) {
        originalTitle?.takeIf { it.isNotBlank() } ?: title
    } else {
        title
    }

    val effectivePosterUrl = when (val lang = settings.posterLanguage) {
        null -> posterUrl
        "original" -> localizedPosters["original"] ?: localizedPosters["null"] ?: posterUrl
        else -> localizedPosters[lang] ?: posterUrl
    }

    val effectiveDescription = when (val lang = settings.overviewLanguage) {
        null -> description
        else -> localizedOverviews[lang]?.takeIf { it.isNotBlank() } ?: description
    }

    val isEnglishOrOriginal = settings.titleMode == TitleDisplayMode.ORIGINAL ||
            settings.uiLocale.startsWith("en", ignoreCase = true)

    val effectiveCast = if (isEnglishOrOriginal && cast.isNotEmpty()) {
        cast.map { actor ->
            val orig = actor.originalName?.takeIf { it.isNotBlank() }
            if (orig != null && orig != actor.name) {
                actor.copy(name = orig)
            } else {
                actor
            }
        }
    } else {
        cast
    }

    return copy(
        title = effectiveTitle,
        posterUrl = effectivePosterUrl,
        description = effectiveDescription,
        cast = effectiveCast
    )
}

fun List<GenreMetadata>.withLocalizedGenres(genreDict: Map<String, String>?): List<GenreMetadata> {
    if (genreDict == null || genreDict.isEmpty()) return this
    return map { g ->
        val locName = genreDict[g.id.toString()]
        if (locName != null && locName.isNotBlank() && locName != "Unknown") {
            g.copy(name = locName)
        } else {
            g
        }
    }
}
