package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.serialization.Serializable

@Serializable
data class AudioTrack(
    val id: String,
    val name: String,
    val language: String? = null,
    val channels: Int? = null,
    val isDefault: Boolean = false,
    val url: String? = null
)

@Serializable
data class SubtitleTrack(
    val id: String,
    val name: String,
    val language: String? = null,
    val isExternal: Boolean = false,
    val url: String? = null
)

@Serializable
data class SubtitleCue(
    val startMs: Long,
    val endMs: Long,
    val text: String
)
