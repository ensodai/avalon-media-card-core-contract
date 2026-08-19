package org.ensodai.avalonmediacard.contract.parsers

object GarbageCollector {
    // Безопасные паттерны очистки
    private val EXTENSION_REGEX = Regex("""\.[a-zA-Z0-9]{2,4}$""")
    private val SQUARE_BRACKETS_REGEX = Regex("""\[[^\]]+\]""")

    // Удаление круглых скобок только если они содержат технический мусор
    private val PARENTHESIZED_TECH_REGEX = Regex(
        """\((?:(?:19|20)\d{2}|\d+p|\d+k|uhd|bluray|hevc|h264|x265)\)""",
        RegexOption.IGNORE_CASE
    )

    // Агрессивные паттерны для вырезания шума из любых мест строки
    private val RESOLUTIONS_REGEX = Regex(
        """\b(?:2160p|1080p|1080i|720p|480p|576p|360p|4k|8k|uhd|fhd|sd|hd|hdtv|web-dl|webdl|bluray|bdrip|brrip|dvdrip|web|webrip|remux|hdtvrip)\b""",
        RegexOption.IGNORE_CASE
    )
    private val DIMENSIONS_REGEX = Regex("""\b\d{3,4}x\d{3,4}\b""", RegexOption.IGNORE_CASE)
    private val CODECS_REGEX = Regex(
        """\b(?:x264|x265|h264|h265|hevc|avc|mpeg4|divx|xvid|10bit|12bit|h\.264|h\.265)\b""",
        RegexOption.IGNORE_CASE
    )
    private val AUDIO_REGEX = Regex(
        """\b(?:ddp5\.1|dd5\.1|ddp2\.0|aac2\.0|aac|dts-hd|dts|ac3|flac|truehd|mp3|ogg|dual|multi|dub|dubbed|sub|subs|subbed|eng|rus|multi-audio|multi-sub)\b""",
        RegexOption.IGNORE_CASE
    )

    // Модернизированное удаление временных диапазонов вида 2011-2013
    private val YEAR_RANGE_REGEX = Regex("""\b(?:19|20)\d{2}\s*-\s*(?:19|20)\d{2}\b""")
    private val YEARS_REGEX = Regex("""\b(?:19|20)\d{2}\b""")

    // Нормализация пробелов и разделителей
    private val CLEAN_SPACES_REGEX = Regex("""[\._\s]+""")
    private val CLEAN_HYPHENS_REGEX = Regex("""\s*-\s*""")

    fun cleanBasic(filename: String): String {
        var name = EXTENSION_REGEX.replace(filename, "")
        name = YEAR_RANGE_REGEX.replace(name, " ")
        name = SQUARE_BRACKETS_REGEX.replace(name, " ")
        name = PARENTHESIZED_TECH_REGEX.replace(name, " ")
        return name.replace(Regex("""\s+"""), " ").trim()
    }

    fun cleanHeavy(filename: String): String {
        var name = cleanBasic(filename)
        name = RESOLUTIONS_REGEX.replace(name, " ")
        name = DIMENSIONS_REGEX.replace(name, " ")
        name = CODECS_REGEX.replace(name, " ")
        name = AUDIO_REGEX.replace(name, " ")
        name = YEARS_REGEX.replace(name, " ")

        name = CLEAN_SPACES_REGEX.replace(name, " ")
        name = CLEAN_HYPHENS_REGEX.replace(name, " - ")
        return name.trim()
    }
}

sealed class MappingResult {
    data class Success(
        val seasons: List<Int>,
        val episodes: List<Int>,
        val isAbsolute: Boolean = false
    ) : MappingResult()

    data class Partial(
        val episodes: List<Int>,
        val isAbsolute: Boolean = false
    ) : MappingResult()

    data class Failed(val reason: String = "No actionable metadata found") : MappingResult()
}

interface EpisodeRegexProvider {
    fun match(filename: String): MappingResult
}

object ParserUtils {
    fun parseRange(startStr: String, endStr: String): List<Int> {
        return try {
            val start = startStr.toInt()
            val end = endStr.toInt()
            if (start <= end && (end - start) < 100) {
                (start..end).toList()
            } else {
                listOf(start, end)
            }
        } catch (e: NumberFormatException) {
            emptyList()
        }
    }
}

class EnglishProvider : EpisodeRegexProvider {
    private val MULTI_SEASON_EP_REGEX =
        Regex("""[Ss]?(\d{1,2})\s*-\s*?(\d{1,2})[._\-\s]*[Ee](\d{1,3})(?:\s*[-_~eE/\\+&]\s*)[Ee]?(\d{1,3})(?!\d)""")
    private val SINGLE_SEASON_RANGE_REGEX =
        Regex("""[Ss]?(\d{1,2})[._\-\s]*[Ee](\d{1,3})(?:\s*[-_~eE/\\+&]\s*)[Ee]?(\d{1,3})(?!\d)""")
    private val LIST_REGEX = Regex("""[Ss]?(\d{1,2})[._\-\s]*((?:[Ee]\d{2,3})+)(?!\d)""")
    private val SINGLE_REGEX = Regex("""[Ss]?(\d{1,2})[._\-\s]*[Ee](\d{1,3})(?!\d)""")
    private val SHORT_RANGE_REGEX = Regex("""(?<!\d)(\d{1,2})[._\-\s]*[xX][._\-\s]*(\d{2,3})\s*-\s*(\d{2,3})(?!\d)""")
    private val SHORT_SINGLE_REGEX = Regex("""(?<!\d)(\d{1,2})[._\-\s]*[xX][._\-\s]*(\d{2,3})(?!\d)""")
    private val PARTIAL_EP_RANGE_REGEX = Regex(
        """(?<!\d)(?:[Ee]p(?:isode)?[._-]?\s*)(\d{1,3})\s*[-_~]\s*[Ee]?[p]?(?:isode)?[._-]?\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val PARTIAL_EP_SINGLE_REGEX =
        Regex("""(?<!\d)(?:[Ee]p(?:isode)?[._-]?\s*)(\d{1,3})(?!\d)""", RegexOption.IGNORE_CASE)

    override fun match(filename: String): MappingResult {
        val cleaned = GarbageCollector.cleanBasic(filename)

        MULTI_SEASON_EP_REGEX.find(cleaned)?.let { match ->
            val seasons = ParserUtils.parseRange(match.groupValues[1], match.groupValues[2])
            val episodes = ParserUtils.parseRange(match.groupValues[3], match.groupValues[4])
            return MappingResult.Success(seasons, episodes)
        }

        SINGLE_SEASON_RANGE_REGEX.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        LIST_REGEX.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val epString = match.groupValues[2]
            val episodes = Regex("""\d+""").findAll(epString).map { it.value.toInt() }.toList()
            return MappingResult.Success(seasons, episodes)
        }

        SINGLE_REGEX.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        SHORT_RANGE_REGEX.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        SHORT_SINGLE_REGEX.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        PARTIAL_EP_RANGE_REGEX.find(cleaned)?.let { match ->
            return MappingResult.Partial(ParserUtils.parseRange(match.groupValues[1], match.groupValues[2]))
        }

        PARTIAL_EP_SINGLE_REGEX.find(cleaned)?.let { match ->
            return MappingResult.Partial(listOf(match.groupValues[1].toInt()))
        }

        return MappingResult.Failed("EnglishProvider found no metadata")
    }
}

class RussianProvider : EpisodeRegexProvider {
    private val CYR_MULTI_SEASON = Regex(
        """(?U)(?<!\d)(\d{1,2})\s*-\s*(\d{1,2})\s*(?:сезон|сезона|сезоны|сс)[,._\s-]*(?:серия|серии|серий|сер|эпизод|эпизоды)[,._\s-]*(\d{1,3})\s*[-_~]\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_MULTI_SEASON_SINGLE_EP = Regex(
        """(?U)(?<!\d)(\d{1,2})\s*-\s*(\d{1,2})\s*(?:сезон|сезона|сезоны|сс)[,._\s-]*(?:серия|серии|серий|сер|эпизод|эпизоды)[,._\s-]*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_RANGE_REGEX =
        Regex("""[Сс]?(\d{1,2})[._\-\s]*[Ее](\d{1,3})(?:\s*[-_~еЕ/\\+&]\s*)[Ее]?(\d{1,3})(?!\d)""")
    private val CYR_LIST_REGEX = Regex("""[Сс]?(\d{1,2})[._\-\s]*((?:[Ее]\d{2,3})+)(?!\d)""")
    private val CYR_SINGLE_REGEX = Regex("""[Сс]?(\d{1,2})[._\-\s]*[Ее](\d{1,3})(?!\d)""")
    private val TEXT_RANGE = Regex(
        """(?U)(?<!\d)(\d{1,2})\s*(?:сезон|сезона|сезоны|сс)[,._\s-]*(?:серия|серии|серий|сер|эпизод|эпизоды)[,._\s-]*(\d{1,3})\s*[-_~]\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val TEXT_SINGLE_1 = Regex(
        """(?U)(?<!\d)(\d{1,2})\s*(?:сезон|сезона|сезоны|сс)[,._\s-]*(?:серия|серии|серий|сер|эпизод|эпизоды)[,._\s-]*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_PARTIAL_RANGE = Regex(
        """(?U)(?<!\d)(?:серия|серии|серий|сер|эпизод|эпизоды)[,._\s-]*(\d{1,3})\s*[-_~]\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_PARTIAL_SINGLE = Regex(
        """(?U)(?<!\d)(?:серия|серии|серий|сер|эпизод|эпизоды)[,._\s-]*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )

    override fun match(filename: String): MappingResult {
        val cleaned = GarbageCollector.cleanBasic(filename)

        CYR_MULTI_SEASON.find(cleaned)?.let { match ->
            val seasons = ParserUtils.parseRange(match.groupValues[1], match.groupValues[2])
            val episodes = ParserUtils.parseRange(match.groupValues[3], match.groupValues[4])
            return MappingResult.Success(seasons, episodes)
        }

        CYR_MULTI_SEASON_SINGLE_EP.find(cleaned)?.let { match ->
            val seasons = ParserUtils.parseRange(match.groupValues[1], match.groupValues[2])
            val episodes = listOf(match.groupValues[3].toInt())
            return MappingResult.Success(seasons, episodes)
        }

        CYR_RANGE_REGEX.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        CYR_LIST_REGEX.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val epString = match.groupValues[2]
            val episodes = Regex("""\d+""").findAll(epString).map { it.value.toInt() }.toList()
            return MappingResult.Success(seasons, episodes)
        }

        CYR_SINGLE_REGEX.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        TEXT_RANGE.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        TEXT_SINGLE_1.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        CYR_PARTIAL_RANGE.find(cleaned)?.let { match ->
            return MappingResult.Partial(ParserUtils.parseRange(match.groupValues[1], match.groupValues[2]))
        }

        CYR_PARTIAL_SINGLE.find(cleaned)?.let { match ->
            return MappingResult.Partial(listOf(match.groupValues[1].toInt()))
        }

        return MappingResult.Failed("RussianProvider found no metadata")
    }
}

class FallbackProvider : EpisodeRegexProvider {
    private val ANIME_RANGE_REGEX = Regex("""\s+-\s+(\d{1,4})\s*-\s*(\d{1,4})(?!\d)""")
    private val ANIME_SINGLE_REGEX = Regex("""\s+-\s+(\d{1,4})(?!\d)""")
    private val NUMERIC_3_4_REGEX = Regex("""(?<!\d)(\d{1,2})(\d{2})(?!\d)""")

    override fun match(filename: String): MappingResult {
        val heavyCleaned = GarbageCollector.cleanHeavy(filename)

        ANIME_RANGE_REGEX.find(heavyCleaned)?.let { match ->
            return MappingResult.Partial(
                episodes = ParserUtils.parseRange(match.groupValues[1], match.groupValues[2]),
                isAbsolute = true
            )
        }

        ANIME_SINGLE_REGEX.find(heavyCleaned)?.let { match ->
            return MappingResult.Partial(
                episodes = listOf(match.groupValues[1].toInt()),
                isAbsolute = true
            )
        }

        NUMERIC_3_4_REGEX.find(heavyCleaned)?.let { match ->
            val season = match.groupValues[1].toInt()
            val episode = match.groupValues[2].toInt()
            if (season in 1..25 && episode in 1..100) {
                return MappingResult.Success(listOf(season), listOf(episode))
            }
        }

        val allNumbers = Regex("""\b\d{1,4}\b""").findAll(heavyCleaned).map { it.value.toInt() }.toList()
        if (allNumbers.isNotEmpty()) {
            val lastNum = allNumbers.last()
            if (lastNum < 1500) {
                return MappingResult.Partial(listOf(lastNum), isAbsolute = true)
            }
        }

        return MappingResult.Failed("FallbackProvider found no actionable numbers")
    }
}

class EpisodeMatcher {
    private val providers = listOf(
        EnglishProvider(),
        RussianProvider(),
        FallbackProvider()
    )

    private fun extractSeasonsContext(text: String): List<Int> {
        for (provider in providers) {
            val match = provider.match(text)
            if (match is MappingResult.Success) {
                return match.seasons
            }
        }

        val rootSeasonRange =
            Regex("""(?U)\b(?:[Ss]easons?|[Сс]езоны?)\s*(\d{1,2})\s*-\s*(\d{1,2})\b""", RegexOption.IGNORE_CASE)
        rootSeasonRange.find(text)?.let { match ->
            return ParserUtils.parseRange(match.groupValues[1], match.groupValues[2])
        }

        val rootSingleSeason = Regex("""(?U)\b(?:[Ss]eason|[Сс]езон)\s*(\d{1,2})\b""", RegexOption.IGNORE_CASE)
        rootSingleSeason.find(text)?.let { match ->
            return listOf(match.groupValues[1].toInt())
        }

        return emptyList()
    }

    fun parseBatch(season: String, episodes: List<String>): Map<String, MappingResult> {
        val rootSeasons = extractSeasonsContext(season)
        val batchResults = mutableMapOf<String, MappingResult>()

        for (filePath in episodes) {
            val fileName = filePath.substringAfterLast('/').substringAfterLast('\\')

            val parentPath = filePath.substringBeforeLast('/', missingDelimiterValue = "")
            val pathSeasons = if (parentPath.isNotEmpty()) extractSeasonsContext(parentPath) else emptyList()

            var fileResult: MappingResult = MappingResult.Failed("Initial state")
            for (provider in providers) {
                val match = provider.match(fileName)
                if (match !is MappingResult.Failed) {
                    fileResult = match
                    break
                }
            }

            batchResults[filePath] = resolveConflict(fileResult, pathSeasons, rootSeasons)
        }

        return batchResults
    }

    fun parse(rootTitle: String, filePath: String): MappingResult {
        return parseBatch(rootTitle, listOf(filePath))[filePath] ?: MappingResult.Failed("Not found")
    }

    private fun resolveConflict(
        fileResult: MappingResult,
        pathSeasons: List<Int>,
        rootSeasons: List<Int>
    ): MappingResult {
        return when (fileResult) {
            is MappingResult.Success -> {
                fileResult
            }

            is MappingResult.Partial -> {
                if (pathSeasons.isNotEmpty()) {
                    MappingResult.Success(pathSeasons, fileResult.episodes, fileResult.isAbsolute)
                } else if (rootSeasons.size == 1) {
                    MappingResult.Success(rootSeasons, fileResult.episodes, fileResult.isAbsolute)
                } else {
                    fileResult
                }
            }

            is MappingResult.Failed -> fileResult
        }
    }
}
