package org.ensodai.avalonmediacard.contract.plugins

import kotlinx.coroutines.flow.Flow

/**
 * Возможность плагина предоставлявать видеопотоки (ссылки, стриминг, торренты).
 */
interface StreamProvider {
    fun findStreams(
        mediaId: String,
        season: Int? = null,
        episode: Int? = null
    ): Flow<MediaStream>
}
