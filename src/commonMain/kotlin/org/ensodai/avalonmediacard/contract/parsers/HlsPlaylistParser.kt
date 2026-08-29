package org.ensodai.avalonmediacard.contract.parsers

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.plugins.AudioTrack
import org.ensodai.avalonmediacard.contract.plugins.SubtitleTrack
import org.ensodai.avalonmediacard.contract.plugins.VideoQuality

/**
 * Result of resolving a master HLS (.m3u8) playlist.
 */
@Serializable
data class HlsResolved(
    val primaryUrl: String,
    val qualityVariants: List<VideoQuality> = emptyList(),
    val audioTracks: List<AudioTrack> = emptyList(),
    val subtitleTracks: List<SubtitleTrack> = emptyList()
)

/**
 * Universal Kotlin Multiplatform parser and resolver for Master HLS (.m3u8) playlists.
 */
object HlsPlaylistParser {

    /**
     * Downloads and resolves master HLS playlist into individual quality streams, audio tracks, and subtitles.
     */
    suspend fun resolveHlsPlaylist(
        httpClient: HttpClient,
        rawHlsUrl: String,
        headers: Map<String, String> = emptyMap()
    ): HlsResolved {
        return try {
            val response = httpClient.get(rawHlsUrl) {
                if (headers.isNotEmpty()) {
                    headers {
                        headers.forEach { (k, v) -> append(k, v) }
                    }
                }
            }
            if (response.status.isSuccess()) {
                val text = response.bodyAsText()
                parseMasterPlaylist(text, rawHlsUrl)
            } else {
                HlsResolved(primaryUrl = rawHlsUrl)
            }
        } catch (_: Exception) {
            HlsResolved(primaryUrl = rawHlsUrl)
        }
    }

    private val RES_REGEX = Regex("""RESOLUTION=(\d+x\d+)""")
    private val BW_REGEX = Regex("""BANDWIDTH=(\d+)""")
    private val NAME_REGEX = Regex("""NAME="([^"]+)"""")
    private val LANG_REGEX = Regex("""LANGUAGE="([^"]+)"""")
    private val GROUP_REGEX = Regex("""GROUP-ID="([^"]+)"""")
    private val URI_REGEX = Regex("""URI="([^"]+)"""")

    /**
     * Parses the string content of a master M3U8 playlist.
     */
    fun parseMasterPlaylist(content: String, baseUrl: String): HlsResolved {
        if (!content.contains("#EXT-X-STREAM-INF") && !content.contains("#EXTM3U")) {
            return HlsResolved(primaryUrl = baseUrl)
        }

        val lines = content.lines()
        val variants = mutableListOf<VideoQuality>()
        val audioTracks = mutableListOf<AudioTrack>()
        val subtitleTracks = mutableListOf<SubtitleTrack>()

        var currentResolution: String? = null

        for (i in lines.indices) {
            val line = lines[i].trim()

            // 1. Video Quality streams
            if (line.startsWith("#EXT-X-STREAM-INF")) {
                val resMatch = RES_REGEX.find(line)
                currentResolution = resMatch?.groupValues?.get(1)?.let { res ->
                    val height = res.substringAfter("x").toIntOrNull()
                    when {
                        height != null && height >= 2160 -> "4K (2160p)"
                        height != null && height >= 1440 -> "2K (1440p)"
                        height != null -> "${height}p"
                        else -> res
                    }
                } ?: run {
                    val bwMatch = BW_REGEX.find(line)
                    val bw = bwMatch?.groupValues?.get(1)?.toLongOrNull()
                    when {
                        bw != null && bw > 15_000_000 -> "4K (2160p)"
                        bw != null && bw > 8_000_000 -> "2K (1440p)"
                        bw != null && bw > 4_500_000 -> "1080p"
                        bw != null && bw > 2_200_000 -> "720p"
                        bw != null && bw > 1_000_000 -> "480p"
                        bw != null && bw > 400_000 -> "360p"
                        bw != null -> "240p"
                        else -> "1080p"
                    }
                }
            } else if (currentResolution != null && (line.startsWith("http") || line.endsWith(".m3u8") || !line.startsWith("#"))) {
                if (line.isNotBlank()) {
                    val resolvedUrl = resolveRelativeUrl(baseUrl, line)
                    variants.add(VideoQuality(label = currentResolution, url = resolvedUrl))
                    currentResolution = null
                }
            }

            // 2. Audio tracks in master playlist (#EXT-X-MEDIA:TYPE=AUDIO...)
            if (line.startsWith("#EXT-X-MEDIA:") && line.contains("TYPE=AUDIO")) {
                val nameMatch = NAME_REGEX.find(line)
                val langMatch = LANG_REGEX.find(line)
                val groupMatch = GROUP_REGEX.find(line)
                val isDefault = line.contains("DEFAULT=YES")
                val uriMatch = URI_REGEX.find(line)

                val name = nameMatch?.groupValues?.get(1) ?: langMatch?.groupValues?.get(1) ?: "Аудио"
                val uri = uriMatch?.groupValues?.get(1)?.let { resolveRelativeUrl(baseUrl, it) }
                val trackId = groupMatch?.groupValues?.get(1) ?: "${audioTracks.size}"

                audioTracks.add(
                    AudioTrack(
                        id = trackId,
                        name = name,
                        language = langMatch?.groupValues?.get(1),
                        isDefault = isDefault,
                        url = uri
                    )
                )
            }

            // 3. Subtitle tracks in master playlist (#EXT-X-MEDIA:TYPE=SUBTITLES...)
            if (line.startsWith("#EXT-X-MEDIA:") && line.contains("TYPE=SUBTITLES")) {
                val nameMatch = NAME_REGEX.find(line)
                val langMatch = LANG_REGEX.find(line)
                val uriMatch = URI_REGEX.find(line)

                val name = nameMatch?.groupValues?.get(1) ?: langMatch?.groupValues?.get(1) ?: "Субтитры"
                val uri = uriMatch?.groupValues?.get(1)?.let { resolveRelativeUrl(baseUrl, it) }

                subtitleTracks.add(
                    SubtitleTrack(
                        id = "${subtitleTracks.size}",
                        name = name,
                        language = langMatch?.groupValues?.get(1),
                        isExternal = true,
                        url = uri
                    )
                )
            }
        }

        val distinctVariants = variants
            .distinctBy { it.label }
            .sortedByDescending { it.label.filter { char -> char.isDigit() }.toIntOrNull() ?: 0 }

        val primary = distinctVariants.firstOrNull()?.url ?: baseUrl

        return HlsResolved(
            primaryUrl = primary,
            qualityVariants = distinctVariants,
            audioTracks = audioTracks.distinctBy { it.name },
            subtitleTracks = subtitleTracks.distinctBy { it.name }
        )
    }

    fun resolveRelativeUrl(baseUrl: String, relativeUrl: String): String {
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl
        }
        if (relativeUrl.startsWith("//")) {
            val scheme = if (baseUrl.startsWith("https:")) "https:" else "http:"
            return "$scheme$relativeUrl"
        }
        val cleanBase = baseUrl.substringBefore("?")
        if (relativeUrl.startsWith("/")) {
            val schemeAndHost = cleanBase.substringBefore("://") + "://" + cleanBase.substringAfter("://").substringBefore("/")
            return "$schemeAndHost$relativeUrl"
        }
        val directory = cleanBase.substringBeforeLast("/", cleanBase)
        return "$directory/$relativeUrl"
    }
}
