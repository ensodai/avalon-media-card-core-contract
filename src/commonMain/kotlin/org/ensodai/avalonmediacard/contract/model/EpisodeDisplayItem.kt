package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable

/**
 * Легковесная модель информации об эпизоде для отображения в UI плеера и списках серий.
 * Не содержит сырых или служебных URL.
 */
@Serializable
data class EpisodeDisplayItem(
    val id: String,
    val title: String,
    val episodeName: String? = null,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val episodesCount: Int? = null,
    val episodesTotal: Int? = null,
    val episodePosterUrl: String? = null,
    val durationSeconds: Double? = null,
    val watchedProgressSeconds: Long? = null,
    val isWatched: Boolean = false,
    val userRating: Int? = null,
    val sourceName: String = ""
)
