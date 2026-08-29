package org.ensodai.avalonmediacard.contract.classification

import kotlinx.serialization.Serializable
import org.ensodai.avalonmediacard.contract.model.MediaMetadata

@Serializable
enum class AnimeSubType {
    JAPANESE_ANIME,
    CHINESE_DONGHUA,
    KOREAN_AENI,
    ANIME_INSPIRED,
    NOT_ANIME
}

@Serializable
data class AnimeDetectionResult(
    val isAnime: Boolean,
    val subType: AnimeSubType,
    val confidence: Float,
    val matchedRules: List<String>
)

/**
 * High-Performance Anime & Donghua Classification Engine.
 * Optimized for Kotlin Multiplatform (Android, JVM, Wasm, JS, Native).
 * Uses O(1) Set lookups and zero-allocation Char codepoint iteration without Regex.
 */
object AnimeClassificationEngine {

    private const val GENRE_ANIMATION = 16

    // Pre-compiled O(1) Lookup Sets (Normalized to lowercase)
    private val JAPANESE_STUDIOS = setOf(
        "toei animation", "madhouse", "mappa", "ufotable", "bones", "kyoto animation",
        "studio ghibli", "sunrise", "a-1 pictures", "cloverworks", "wit studio",
        "production i.g", "j.c.staff", "studio deen", "pierrot", "shaft", "trigger",
        "science saru", "comix wave films", "david production", "tms entertainment",
        "feel.", "doga kobo", "p.a. works", "silver link", "tatsunoko production",
        "olm", "kinema citrus", "polygon pictures", "studio ponoc", "bandai namco filmworks",
        "8bit", "actas", "aic", "artland", "asahi production", "asread", "bibury animation studios",
        "brain's base", "bridge", "c2c", "connect", "cygamespictures", "diomedea", "drive",
        "engi", "fanworks", "felix film", "gainax", "gallop", "geno studio", "geek toys",
        "gohands", "gonzo", "graphinica", "hoods entertainment", "imagin", "khara",
        "lapin track", "lay-duce", "lerche", "liden films", "magic bus", "millepensee",
        "mushi production", "naz", "nexus", "nippon animation", "nomad", "nut", "orange",
        "ordet", "passione", "pine jam", "platinum vision", "project no.9", "revoroot",
        "sanzigen", "seven arcs", "shuka", "signal.md", "studio 3hz", "studio 4°c",
        "studio bind", "studio blanc", "studio chizu", "studio colorido", "studio comet",
        "studio fantasi", "studio gokumi", "studio hibari", "studio kai", "studio puyukai",
        "studio voln", "synergysp", "tear studio", "tezuka productions", "triangle staff",
        "troyca", "white fox", "yokohama animation laboratory", "yumeta company", "zexcs", "zero-g"
    )

    private val CHINESE_STUDIOS = setOf(
        "bilibili", "tencent video", "haoliners animation league", "sparkly key animation",
        "foch film", "light chaser animation studios", "lan studio", "cmc media",
        "big firebird culture", "be dream", "210 animation", "paper plane animation studio",
        "base fx", "winsing animation", "motion magic", "cg year", "djinn power",
        "kuaikan comics", "ruo hong culture", "garden culture", "pb animation", "liyu culture"
    )

    private val KOREAN_STUDIOS = setOf(
        "dr movie", "studio mir", "red dog culture house", "locus corporation",
        "studio animal", "iconix entertainment", "roi visual", "marmalade boy", "d&c media"
    )

    private val WESTERN_ANIME_INSPIRED_STUDIOS = setOf(
        "powerhouse animation studios", "fortiche production", "rooster teeth",
        "flying bark productions", "titmouse", "frederator studios"
    )

    private val JAPANESE_NETWORKS = setOf(
        "tokyo mx", "tv tokyo", "at-x", "bs11", "mbs", "fuji tv", "nhk",
        "nippon television", "kansai telecasting", "animax", "cbc", "sun tv",
        "kbs kyoto", "tva", "tvk", "htv", "abn", "bs4", "chiba tv", "ytv"
    )

    private val CHINESE_NETWORKS = setOf(
        "bilibili", "tencent video", "iqiyi", "youku", "cctv"
    )

    private val ANIME_KEYWORDS = setOf(
        "anime", "isekai", "shounen", "shoujo", "seinen", "mecha", "otaku",
        "based on manga", "based on light novel", "magical girl", "yuri", "yaoi", "harem"
    )

    private val DONGHUA_KEYWORDS = setOf(
        "donghua", "wuxia", "xianxia", "cultivation", "chinese animation", "based on manhua", "martial arts"
    )

    private val AENI_KEYWORDS = setOf(
        "aeni", "based on webtoon", "based on manhwa", "korean animation"
    )

    fun analyze(
        genres: List<Int>,
        keywords: List<String> = emptyList(),
        productionCompanies: List<String> = emptyList(),
        productionCountries: List<String> = emptyList(),
        originalLanguage: String? = null,
        networks: List<String> = emptyList(),
        originalTitle: String? = null,
        title: String? = null
    ): AnimeDetectionResult {
        val rules = mutableListOf<String>()
        var confidence = 0.0f
        var subType = AnimeSubType.NOT_ANIME

        // Tier 1: Deterministic Animation Genre gate (16)
        if (!genres.contains(GENRE_ANIMATION)) {
            rules.add("REJECTED: Missing TMDB Animation Genre (16)")
            return AnimeDetectionResult(false, AnimeSubType.NOT_ANIME, 0.0f, rules)
        }

        val normalizedStudios = productionCompanies.map { it.lowercase().trim() }
        val normalizedKeywords = keywords.map { it.lowercase().trim() }
        val normalizedNetworks = networks.map { it.lowercase().trim() }
        val lang = originalLanguage?.lowercase()?.trim()
        val countries = productionCountries.map { it.uppercase().trim() }

        // Tier 2: Studio Matching
        val hasJpStudio = normalizedStudios.any { studio -> JAPANESE_STUDIOS.any { studio.contains(it) } }
        val hasCnStudio = normalizedStudios.any { studio -> CHINESE_STUDIOS.any { studio.contains(it) } }
        val hasKrStudio = normalizedStudios.any { studio -> KOREAN_STUDIOS.any { studio.contains(it) } }
        val hasWesternAnimeStudio = normalizedStudios.any { studio -> WESTERN_ANIME_INSPIRED_STUDIOS.any { studio.contains(it) } }

        val isWesternOrigin = countries.contains("US") || countries.contains("FR") || lang == "en"

        if (hasJpStudio) {
            subType = AnimeSubType.JAPANESE_ANIME
            confidence = 1.0f
            rules.add("DETERMINISTIC: Japanese Animation Studio Matched")
        } else if (hasCnStudio) {
            subType = AnimeSubType.CHINESE_DONGHUA
            confidence = 1.0f
            rules.add("DETERMINISTIC: Chinese Donghua Studio Matched")
        } else if (hasKrStudio) {
            if (isWesternOrigin) {
                subType = AnimeSubType.ANIME_INSPIRED
                confidence = 0.9f
                rules.add("MATCH: Western Anime-Inspired Production (Korean Animation Contracted)")
            } else {
                subType = AnimeSubType.KOREAN_AENI
                confidence = 1.0f
                rules.add("DETERMINISTIC: Korean Animation Studio Matched")
            }
        }

        // Tier 3: Network, Language, and Country Combination
        if (confidence < 0.9f) {
            val hasJpNetwork = normalizedNetworks.any { net -> JAPANESE_NETWORKS.any { net.contains(it) } }
            val hasCnNetwork = normalizedNetworks.any { net -> CHINESE_NETWORKS.any { net.contains(it) } }

            if (hasJpNetwork) {
                subType = AnimeSubType.JAPANESE_ANIME
                confidence = 0.9f
                rules.add("HIGH CONFIDENCE: Japanese Network Matched")
            } else if (hasCnNetwork) {
                subType = AnimeSubType.CHINESE_DONGHUA
                confidence = 0.9f
                rules.add("HIGH CONFIDENCE: Chinese Network Matched")
            } else if (lang == "ja" || countries.contains("JP")) {
                subType = AnimeSubType.JAPANESE_ANIME
                confidence += 0.7f
                rules.add("MODERATE: Japanese Language / Country Matched")
            } else if (lang == "zh" || countries.contains("CN")) {
                subType = AnimeSubType.CHINESE_DONGHUA
                confidence += 0.7f
                rules.add("MODERATE: Chinese Language / Country Matched")
            } else if (lang == "ko" || countries.contains("KR")) {
                subType = AnimeSubType.KOREAN_AENI
                confidence += 0.7f
                rules.add("MODERATE: Korean Language / Country Matched")
            }
        }

        // Tier 4: Keywords
        val hasAnimeKeyword = normalizedKeywords.any { kw -> ANIME_KEYWORDS.any { kw.contains(it) } }
        val hasDonghuaKeyword = normalizedKeywords.any { kw -> DONGHUA_KEYWORDS.any { kw.contains(it) } }
        val hasAeniKeyword = normalizedKeywords.any { kw -> AENI_KEYWORDS.any { kw.contains(it) } }
        val hasAnimeInspiredKeyword = normalizedKeywords.any { it.contains("anime inspired") || it.contains("anime style") }

        if (hasAnimeKeyword && subType == AnimeSubType.NOT_ANIME) {
            subType = AnimeSubType.JAPANESE_ANIME
            confidence += 0.5f
            rules.add("KEYWORD: Anime Tag Matched")
        } else if (hasDonghuaKeyword && subType == AnimeSubType.NOT_ANIME) {
            subType = AnimeSubType.CHINESE_DONGHUA
            confidence += 0.5f
            rules.add("KEYWORD: Donghua Tag Matched")
        } else if (hasAeniKeyword && subType == AnimeSubType.NOT_ANIME) {
            subType = AnimeSubType.KOREAN_AENI
            confidence += 0.5f
            rules.add("KEYWORD: Aeni Tag Matched")
        }

        // Tier 5: Script Detection (Hiragana, Katakana, Kanji, Hanzi, Hangul)
        if (confidence < 0.9f) {
            val titleToCheck = originalTitle ?: title
            if (titleToCheck != null) {
                when (detectAsianScript(titleToCheck)) {
                    AsianScript.JAPANESE -> {
                        if (subType == AnimeSubType.NOT_ANIME || subType == AnimeSubType.JAPANESE_ANIME) {
                            subType = AnimeSubType.JAPANESE_ANIME
                            confidence += 0.8f
                            rules.add("SCRIPT: Japanese Kana/Kanji Detected")
                        }
                    }
                    AsianScript.CHINESE -> {
                        if (subType == AnimeSubType.NOT_ANIME || subType == AnimeSubType.CHINESE_DONGHUA) {
                            subType = AnimeSubType.CHINESE_DONGHUA
                            confidence += 0.7f
                            rules.add("SCRIPT: Chinese Hanzi Detected")
                        }
                    }
                    AsianScript.KOREAN -> {
                        if (subType == AnimeSubType.NOT_ANIME || subType == AnimeSubType.KOREAN_AENI) {
                            subType = AnimeSubType.KOREAN_AENI
                            confidence += 0.8f
                            rules.add("SCRIPT: Korean Hangul Detected")
                        }
                    }
                    AsianScript.NONE -> {}
                }
            }
        }

        // Tier 6: Western Anime-Inspired Override
        if (hasWesternAnimeStudio || (isWesternOrigin && (hasAnimeKeyword || hasAnimeInspiredKeyword))) {
            subType = AnimeSubType.ANIME_INSPIRED
            confidence = 0.9f
            rules.add("OVERRIDE: Western Anime-Inspired Studio / Style Matched")
        }

        confidence = confidence.coerceAtMost(1.0f)
        val isAnimeCategory = (subType != AnimeSubType.NOT_ANIME && confidence >= 0.7f)
        if (!isAnimeCategory) {
            subType = AnimeSubType.NOT_ANIME
            rules.add("VERDICT: Below Confidence Threshold")
        }

        return AnimeDetectionResult(
            isAnime = isAnimeCategory,
            subType = subType,
            confidence = confidence,
            matchedRules = rules
        )
    }

    fun analyze(metadata: MediaMetadata): AnimeDetectionResult {
        return analyze(
            genres = metadata.genres.map { it.id },
            keywords = metadata.keywords.map { it.name },
            productionCompanies = metadata.productionCompanies.map { it.name },
            productionCountries = emptyList(),
            originalLanguage = null,
            networks = listOfNotNull(metadata.network),
            originalTitle = metadata.originalTitle,
            title = metadata.title
        )
    }

    private enum class AsianScript { JAPANESE, CHINESE, KOREAN, NONE }

    private fun detectAsianScript(text: String): AsianScript {
        var hasHiraganaOrKatakana = false
        var hasHanziOrKanji = false
        var hasHangul = false

        for (i in text.indices) {
            val code = text[i].code
            if (code in 0x3040..0x30FF) {
                hasHiraganaOrKatakana = true
            } else if (code in 0x4E00..0x9FFF) {
                hasHanziOrKanji = true
            } else if (code in 0xAC00..0xD7AF) {
                hasHangul = true
            }
        }

        return when {
            hasHangul -> AsianScript.KOREAN
            hasHiraganaOrKatakana -> AsianScript.JAPANESE
            hasHanziOrKanji -> AsianScript.CHINESE
            else -> AsianScript.NONE
        }
    }
}
