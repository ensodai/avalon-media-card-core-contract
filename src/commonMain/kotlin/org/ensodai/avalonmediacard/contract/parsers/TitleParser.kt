package org.ensodai.avalonmediacard.contract.parsers

object TitleParser {
    fun parseQuality(title: String): String? {
        val lower = title.lowercase()
        return when {
            "2160p" in lower || "4k" in lower || "uhd" in lower -> "2160p (4K)"
            "1080p" in lower || "fhd" in lower -> "1080p"
            "720p" in lower || "hd" in lower -> "720p"
            "480p" in lower || "dvd" in lower -> "480p"
            else -> null
        }
    }

    fun parseCodec(title: String): String? {
        val lower = title.lowercase()
        return when {
            "x265" in lower || "h265" in lower || "hevc" in lower -> "HEVC (x265)"
            "x264" in lower || "h264" in lower || "avc" in lower -> "AVC (x264)"
            "av1" in lower -> "AV1"
            else -> null
        }
    }

    fun parseFormat(title: String): String? {
        val lower = title.lowercase()
        return when {
            ".mkv" in lower || "mkv" in lower.split(Regex("[^a-z0-9]")) -> "MKV"
            ".mp4" in lower || "mp4" in lower.split(Regex("[^a-z0-9]")) -> "MP4"
            ".avi" in lower || "avi" in lower.split(Regex("[^a-z0-9]")) -> "AVI"
            ".ts" in lower || "ts" in lower.split(Regex("[^a-z0-9]")) -> "TS"
            else -> null
        }
    }

    fun parseAudio(title: String): String? {
        val lower = title.lowercase()
        return when {
            "dts-hd" in lower -> "DTS-HD"
            "dts" in lower -> "DTS"
            "truehd" in lower -> "TrueHD"
            "atmos" in lower -> "Atmos"
            "ac3" in lower -> "AC3"
            "aac" in lower -> "AAC"
            "flac" in lower -> "FLAC"
            else -> null
        }
    }

    fun parseIsHdr(title: String): Boolean {
        val lower = title.lowercase()
        return "hdr" in lower || "dolby vision" in lower || "dv" in lower.split(Regex("[^a-z0-9]"))
    }

    fun parseEpisodePattern(title: String): Int? {
        val lower = title.lowercase()
        val regex = Regex("""(?:e|ep|episode|серия)\s*(\d+)""")
        val match = regex.find(lower)
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
