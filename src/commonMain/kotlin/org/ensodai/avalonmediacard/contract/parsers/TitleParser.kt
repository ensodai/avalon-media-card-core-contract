package org.ensodai.avalonmediacard.contract.parsers

object TitleParser {
    private val NON_ALPHANUMERIC_REGEX = Regex("[^a-z0-9]")
    private val EPISODE_PATTERN_REGEX = Regex("""(?:e|ep|episode|серия)\s*(\d+)""")

    private val QUALITY_2160_REGEX = Regex("""\b(?:2160p|4k|uhd)\b""")
    private val QUALITY_1080_REGEX = Regex("""\b(?:1080p|fhd)\b""")
    private val QUALITY_720_REGEX = Regex("""\b(?:720p|hd)\b""")
    private val QUALITY_480_REGEX = Regex("""\b(?:480p|dvd)\b""")

    private val CODEC_HEVC_REGEX = Regex("""\b(?:x265|h265|hevc)\b""")
    private val CODEC_AVC_REGEX = Regex("""\b(?:x264|h264|avc)\b""")
    private val CODEC_AV1_REGEX = Regex("""\bav1\b""")

    private val AUDIO_DTS_HD_REGEX = Regex("""\bdts-hd\b""")
    private val AUDIO_DTS_REGEX = Regex("""\bdts\b""")
    private val AUDIO_TRUEHD_REGEX = Regex("""\btruehd\b""")
    private val AUDIO_ATMOS_REGEX = Regex("""\batmos\b""")
    private val AUDIO_AC3_REGEX = Regex("""\bac3\b""")
    private val AUDIO_AAC_REGEX = Regex("""\baac\b""")
    private val AUDIO_FLAC_REGEX = Regex("""\bflac\b""")

    private val HDR_REGEX = Regex("""\b(?:hdr|dolby\s*vision|dv)\b""")

    fun parseQuality(title: String): String? {
        val lower = title.lowercase()
        return when {
            QUALITY_2160_REGEX.containsMatchIn(lower) -> "2160p (4K)"
            QUALITY_1080_REGEX.containsMatchIn(lower) -> "1080p"
            QUALITY_720_REGEX.containsMatchIn(lower) -> "720p"
            QUALITY_480_REGEX.containsMatchIn(lower) -> "480p"
            else -> null
        }
    }

    fun parseCodec(title: String): String? {
        val lower = title.lowercase()
        return when {
            CODEC_HEVC_REGEX.containsMatchIn(lower) -> "HEVC (x265)"
            CODEC_AVC_REGEX.containsMatchIn(lower) -> "AVC (x264)"
            CODEC_AV1_REGEX.containsMatchIn(lower) -> "AV1"
            else -> null
        }
    }

    fun parseFormat(title: String): String? {
        val lower = title.lowercase()
        val tokens = lower.split(NON_ALPHANUMERIC_REGEX)
        return when {
            ".mkv" in lower || "mkv" in tokens -> "MKV"
            ".mp4" in lower || "mp4" in tokens -> "MP4"
            ".avi" in lower || "avi" in tokens -> "AVI"
            ".ts" in lower || "ts" in tokens -> "TS"
            else -> null
        }
    }

    fun parseAudio(title: String): String? {
        val lower = title.lowercase()
        return when {
            AUDIO_DTS_HD_REGEX.containsMatchIn(lower) -> "DTS-HD"
            AUDIO_DTS_REGEX.containsMatchIn(lower) -> "DTS"
            AUDIO_TRUEHD_REGEX.containsMatchIn(lower) -> "TrueHD"
            AUDIO_ATMOS_REGEX.containsMatchIn(lower) -> "Atmos"
            AUDIO_AC3_REGEX.containsMatchIn(lower) -> "AC3"
            AUDIO_AAC_REGEX.containsMatchIn(lower) -> "AAC"
            AUDIO_FLAC_REGEX.containsMatchIn(lower) -> "FLAC"
            else -> null
        }
    }

    fun parseIsHdr(title: String): Boolean {
        val lower = title.lowercase()
        return HDR_REGEX.containsMatchIn(lower) || "dolby vision" in lower
    }

    fun parseEpisodePattern(title: String): Int? {
        val lower = title.lowercase()
        val match = EPISODE_PATTERN_REGEX.find(lower)
        return match?.groupValues?.get(1)?.toIntOrNull()
    }

    fun getQualityWeight(quality: String?): Int {
        return when (quality) {
            "2160p (4K)" -> 4
            "1080p" -> 3
            "720p" -> 2
            "480p" -> 1
            else -> 0
        }
    }
}
