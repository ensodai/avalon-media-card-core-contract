package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.UiComponent
import org.ensodai.avalonmediacard.contract.UiModifier
import kotlin.uuid.Uuid

@Serializable
data class MediaRating(
    val source: String,
    val value: String,
    val colorHex: String? = null
)

@Serializable
data class UiMediaHeader(
    val title: String,
    val subtitle: String? = null,
    val tagline: String? = null,
    val rating: Double? = null,
    val ratings: List<MediaRating> = emptyList(),
    val genres: List<String> = emptyList(),
    val releaseDate: String? = null,
    val posterUrl: String? = null,
    val backgroundUrl: String? = null,
    val directorName: String? = null,
    val directorId: String? = null,
    val directorImageUrl: String? = null,
    val trailerUrl: String? = null,
    val isLoading: Boolean = false,
    val catalogId: String? = null,
    val inCollection: Boolean = false,
    val mediaId: String? = null,
    override val id: Uuid? = null,
    override val modifiers: List<UiModifier> = emptyList()
) : UiComponent()
