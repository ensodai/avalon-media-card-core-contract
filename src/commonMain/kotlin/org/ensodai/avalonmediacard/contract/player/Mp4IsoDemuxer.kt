package org.ensodai.avalonmediacard.contract.player

import org.ensodai.avalonmediacard.contract.plugins.AudioTrack
import org.ensodai.avalonmediacard.contract.plugins.SubtitleTrack

data class Mp4DemuxResult(
    val audioTracks: List<AudioTrack>,
    val subtitleTracks: List<SubtitleTrack>
)

object Mp4IsoDemuxer {

    fun parseHeader(bytes: ByteArray): Mp4DemuxResult {
        val audioTracks = mutableListOf<AudioTrack>()
        val subtitleTracks = mutableListOf<SubtitleTrack>()

        var index = 0
        while (index <= bytes.size - 8) {
            val boxSize = readInt32(bytes, index).toLong() and 0xFFFFFFFFL
            val boxType = readString(bytes, index + 4, 4)

            if (boxSize < 8 && boxSize != 1L && boxSize != 0L) {
                index += 4
                continue
            }

            val actualSize = if (boxSize == 1L && index + 16 <= bytes.size) {
                readInt64(bytes, index + 8)
            } else boxSize

            if (boxType == "moov" || boxType == "trak" || boxType == "mdia" || boxType == "minf" || boxType == "stbl") {
                index += 8
                continue
            }

            if (boxType == "hdlr" && index + 16 <= bytes.size) {
                val handlerType = readString(bytes, index + 16, 4)
                val trackNum = audioTracks.size + subtitleTracks.size + 1

                if (handlerType == "soun") {
                    audioTracks.add(
                        AudioTrack(
                            id = "mp4_audio_$trackNum",
                            name = "Audio Track #$trackNum",
                            language = "und",
                            channels = 2,
                            isDefault = audioTracks.isEmpty()
                        )
                    )
                } else if (handlerType == "subt" || handlerType == "text" || handlerType == "sbtl") {
                    subtitleTracks.add(
                        SubtitleTrack(
                            id = "mp4_sub_$trackNum",
                            name = "Subtitle Track #$trackNum",
                            language = "und",
                            isExternal = false,
                            url = null
                        )
                    )
                }
            }

            val advance = if (actualSize > 8) actualSize.toInt() else 8
            index += advance.coerceAtLeast(8)
        }

        return Mp4DemuxResult(audioTracks, subtitleTracks)
    }

    private fun readInt32(bytes: ByteArray, offset: Int): Int {
        if (offset + 4 > bytes.size) return 0
        return ((bytes[offset].toInt() and 0xFF) shl 24) or
                ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
                ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
                (bytes[offset + 3].toInt() and 0xFF)
    }

    private fun readInt64(bytes: ByteArray, offset: Int): Long {
        if (offset + 8 > bytes.size) return 0L
        var res = 0L
        for (i in 0 until 8) {
            res = (res shl 8) or (bytes[offset + i].toLong() and 0xFFL)
        }
        return res
    }

    private fun readString(bytes: ByteArray, offset: Int, length: Int): String {
        val actualLen = length.coerceAtMost(bytes.size - offset)
        if (actualLen <= 0) return ""
        return bytes.copyOfRange(offset, offset + actualLen).decodeToString()
    }
}
