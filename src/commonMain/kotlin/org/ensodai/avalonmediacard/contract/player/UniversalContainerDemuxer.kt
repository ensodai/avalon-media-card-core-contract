package org.ensodai.avalonmediacard.contract.player

import org.ensodai.avalonmediacard.contract.plugins.AudioTrack
import org.ensodai.avalonmediacard.contract.plugins.SubtitleTrack

data class UniversalDemuxResult(
    val audioTracks: List<AudioTrack>,
    val subtitleTracks: List<SubtitleTrack>
)

object UniversalContainerDemuxer {

    fun parseHeader(bytes: ByteArray): UniversalDemuxResult {
        if (bytes.size >= 4 && bytes[0] == 0x1A.toByte() && bytes[1] == 0x45.toByte() && bytes[2] == 0xDF.toByte() && bytes[3] == 0xA3.toByte()) {
            val mkvRes = MatroskaEbmlDemuxer.parseHeader(bytes)
            return UniversalDemuxResult(mkvRes.audioTracks, mkvRes.subtitleTracks)
        }

        val mp4Res = Mp4IsoDemuxer.parseHeader(bytes)
        if (mp4Res.audioTracks.isNotEmpty() || mp4Res.subtitleTracks.isNotEmpty()) {
            return UniversalDemuxResult(mp4Res.audioTracks, mp4Res.subtitleTracks)
        }

        return UniversalDemuxResult(emptyList(), emptyList())
    }
}
