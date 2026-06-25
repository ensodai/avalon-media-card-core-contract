package org.ensodai.avalonmediacard.contract

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.annotations.Rpc
import org.ensodai.avalonmediacard.contract.plugins.MediaStream

@Rpc
interface PlayerRpcService {
    fun streamVideoSources(mediaId: String, season: Int? = null, episode: Int? = null): Flow<MediaStream>
}
