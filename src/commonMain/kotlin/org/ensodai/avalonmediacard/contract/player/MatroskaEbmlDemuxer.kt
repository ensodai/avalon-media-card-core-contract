package org.ensodai.avalonmediacard.contract.player

import org.ensodai.avalonmediacard.contract.plugins.AudioTrack
import org.ensodai.avalonmediacard.contract.plugins.SubtitleTrack

data class MatroskaDemuxResult(
    val audioTracks: List<AudioTrack>,
    val subtitleTracks: List<SubtitleTrack>
)

object MatroskaEbmlDemuxer {

    fun parseHeader(bytes: ByteArray): MatroskaDemuxResult {
        val audioTracks = mutableListOf<AudioTrack>()
        val subtitleTracks = mutableListOf<SubtitleTrack>()

        var index = 0

        while (index < bytes.size - 4) {
            val id = readElementId(bytes, index)
            if (id == null) {
                index++
                continue
            }

            val (elementId, idLen) = id
            index += idLen

            val vintLen = readVint(bytes, index)
            if (vintLen == null) {
                index++
                continue
            }

            val (length, lenBytes) = vintLen
            index += lenBytes

            if (elementId == 0x1A45DFA3 || elementId == 0x18538067 || elementId == 0x1654AE6B) {
                // Container tags: step inside
                continue
            }

            if (elementId == 0xAE) { // TrackEntry tag
                val endPos = (index + length.toInt()).coerceAtMost(bytes.size)
                val trackEntryBytes = bytes.copyOfRange(index, endPos)
                parseTrackEntry(trackEntryBytes, audioTracks, subtitleTracks)
                index = endPos
            } else if (length > 0) {
                index = (index + length.toInt()).coerceAtMost(bytes.size)
            }
        }

        return MatroskaDemuxResult(audioTracks, subtitleTracks)
    }

    private fun parseTrackEntry(
        entryBytes: ByteArray,
        audioTracks: MutableList<AudioTrack>,
        subtitleTracks: MutableList<SubtitleTrack>
    ) {
        var idx = 0
        var trackType = -1
        var codecId = ""
        var language = "und"
        var name = ""

        while (idx < entryBytes.size - 2) {
            val id = readElementId(entryBytes, idx) ?: break
            val (elementId, idLen) = id
            idx += idLen

            val vint = readVint(entryBytes, idx) ?: break
            val (len, lenBytes) = vint
            idx += lenBytes

            val dataLen = len.toInt().coerceAtMost(entryBytes.size - idx)

            when (elementId) {
                0x83 -> { // TrackType
                    if (dataLen > 0) trackType = entryBytes[idx].toInt() and 0xFF
                }
                0x86 -> { // CodecID
                    codecId = entryBytes.copyOfRange(idx, idx + dataLen).decodeToString()
                }
                0x22B59C -> { // Language
                    language = entryBytes.copyOfRange(idx, idx + dataLen).decodeToString()
                }
                0x536E -> { // Name
                    name = entryBytes.copyOfRange(idx, idx + dataLen).decodeToString()
                }
            }
            idx += dataLen
        }

        if (trackType == 2) { // Audio
            val num = audioTracks.size + 1
            val labelName = if (name.isNotBlank()) name else "Audio #$num ($codecId)"
            audioTracks.add(
                AudioTrack(
                    id = "audio_$num",
                    name = labelName,
                    language = language,
                    channels = 2,
                    isDefault = audioTracks.isEmpty()
                )
            )
        } else if (trackType == 17 || trackType == 0x11) { // Subtitle
            val num = subtitleTracks.size + 1
            val labelName = if (name.isNotBlank()) "$name ($language)" else "Subtitle #$num ($language)"
            subtitleTracks.add(
                SubtitleTrack(
                    id = "sub_$num",
                    name = labelName,
                    language = language,
                    isExternal = false,
                    url = null
                )
            )
        }
    }

    private fun readElementId(bytes: ByteArray, offset: Int): Pair<Int, Int>? {
        if (offset >= bytes.size) return null
        val b0 = bytes[offset].toInt() and 0xFF
        return when {
            (b0 and 0x80) != 0 -> Pair(b0, 1)
            (b0 and 0x40) != 0 -> {
                if (offset + 1 >= bytes.size) null
                else Pair((b0 shl 8) or (bytes[offset + 1].toInt() and 0xFF), 2)
            }
            (b0 and 0x20) != 0 -> {
                if (offset + 2 >= bytes.size) null
                else Pair((b0 shl 16) or ((bytes[offset + 1].toInt() and 0xFF) shl 8) or (bytes[offset + 2].toInt() and 0xFF), 3)
            }
            (b0 and 0x10) != 0 -> {
                if (offset + 3 >= bytes.size) null
                else Pair((b0 shl 24) or ((bytes[offset + 1].toInt() and 0xFF) shl 16) or ((bytes[offset + 2].toInt() and 0xFF) shl 8) or (bytes[offset + 3].toInt() and 0xFF), 4)
            }
            else -> null
        }
    }

    private fun readVint(bytes: ByteArray, offset: Int): Pair<Long, Int>? {
        if (offset >= bytes.size) return null
        val b0 = bytes[offset].toInt() and 0xFF
        var length = 0
        var mask = 0x80

        for (i in 1..8) {
            if ((b0 and mask) != 0) {
                length = i
                break
            }
            mask = mask shr 1
        }

        if (length == 0 || offset + length > bytes.size) return null

        var value = (b0 and (mask - 1)).toLong()
        for (i in 1 until length) {
            value = (value shl 8) or (bytes[offset + i].toLong() and 0xFFL)
        }

        return Pair(value, length)
    }
}
