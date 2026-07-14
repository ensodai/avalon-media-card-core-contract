package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.serialization.Serializable

@Serializable
data class StreamCursor(
    val season: Int,
    val episode: Int,
    val progressSeconds: Long? = null,
    val episodeName: String? = null
)
