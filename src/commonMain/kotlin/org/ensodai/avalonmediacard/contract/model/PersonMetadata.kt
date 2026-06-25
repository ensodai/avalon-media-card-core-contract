package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable

@Serializable
data class FilmographyCredit(
    val mediaId: String,
    val title: String,
    val posterUrl: String? = null,
    val releaseDate: String? = null,
    val mediaType: String, // "movie" or "tv"
    val department: String, // "Directing", "Writing", "Production", "Acting"
    val jobOrCharacter: String? = null
)

@Serializable
data class PersonMetadata(
    val name: String,
    val biography: String? = null,
    val birthday: String? = null,
    val deathday: String? = null,
    val placeOfBirth: String? = null,
    val profileUrl: String? = null,
    val knownForDepartment: String? = null,
    val externalIds: Map<String, String> = emptyMap(),
    val images: List<String> = emptyList(),
    val filmography: List<FilmographyCredit> = emptyList()
)
