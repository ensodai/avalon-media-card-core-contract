package org.ensodai.avalonmediacard.contract

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class MediaMetadataCacheItem(
    val id: String,
    val catalogId: String, // "tmdb", "myshows" и др.
    val externalId: String,
    val title: String,
    val overview: String? = null,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val rating: Double? = null,
    val metadataJson: String? = null,
    val cachedAt: Instant
)
