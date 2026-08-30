package org.ensodai.avalonmediacard.contract.parsers

object GarbageCollector {
    private val EXTENSION_REGEX = Regex("""\.[a-zA-Z0-9]{2,4}$""")
    private val SQUARE_BRACKETS_TECH_REGEX = Regex(
        """\[(?:(?:19|20)\d{2}|\d+p|\d+k|uhd|bluray|hevc|h264|x265|dvdrip|web-dl|webdl|webrip|bdrip|remux|hdtv|hdtvrip|lossless|rus|eng|multi|sub|dub|itunes)[^\]]*\]""",
        RegexOption.IGNORE_CASE
    )

    private val PARENTHESIZED_TECH_REGEX = Regex(
        """\((?:(?:19|20)\d{2}|\d+p|\d+k|uhd|bluray|hevc|h264|x265|\d+\s*(?:аудио|дорож|голос|звук|sub|subs|voice|track|tracks)[^)]*|дубляж|мво|дво|itunes|звук|перевод)[^)]*\)""",
        RegexOption.IGNORE_CASE
    )

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

    private val YEAR_RANGE_REGEX = Regex("""\b(?:19|20)\d{2}\s*-\s*(?:19|20)\d{2}\b""")
    private val YEARS_REGEX = Regex("""\b(?:19|20)\d{2}\b""")

    private val CLEAN_SPACES_REGEX = Regex("""[\._\s]+""")
    private val CLEAN_HYPHENS_REGEX = Regex("""\s*-\s*""")

    private val MULTI_SPACE_REGEX = Regex("""\s+""")

    fun cleanBasic(filename: String): String {
        var name = EXTENSION_REGEX.replace(filename, "")
        name = YEAR_RANGE_REGEX.replace(name, " ")
        name = SQUARE_BRACKETS_TECH_REGEX.replace(name, " ")
        name = PARENTHESIZED_TECH_REGEX.replace(name, " ")
        // Unwrap brackets/quotes into spaces to preserve inner numbers
        name = name.replace('[', ' ').replace(']', ' ')
            .replace('{', ' ').replace('}', ' ')
            .replace('«', ' ').replace('»', ' ')
        return MULTI_SPACE_REGEX.replace(name, " ").trim()
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
            if (start <= end && (end - start) < 150) {
                (start..end).toList()
            } else {
                listOf(start, end)
            }
        } catch (e: NumberFormatException) {
            emptyList()
        }
    }

    fun parseRomanNumeral(str: String): Int? {
        return when (str.uppercase()) {
            "I" -> 1
            "II" -> 2
            "III" -> 3
            "IV" -> 4
            "V" -> 5
            "VI" -> 6
            "VII" -> 7
            "VIII" -> 8
            "IX" -> 9
            "X" -> 10
            else -> null
        }
    }
}

class EnglishProvider : EpisodeRegexProvider {
    private val MULTI_SEASON_EP_REGEX =
        Regex("""(?<!\w)[Ss](\d{1,2})\s*-\s*?[Ss]?(\d{1,2})[._\-\s]*[Ee](\d{1,3})(?:\s*[-_~eE/\\+&.]\s*)[Ee]?(\d{1,3})(?!\d)""")
    private val SINGLE_SEASON_RANGE_REGEX =
        Regex("""(?<!\w)[Ss](\d{1,2})[._\-\s]*[Ee](\d{1,3})(?:\s*(?:[-_~eE/\\+&]|\.[Ee]|_[Ee]?)\s*)[Ee]?(\d{1,3})(?!\d)""")
    private val LIST_REGEX = Regex("""(?<!\w)[Ss](\d{1,2})[._\-\s]*((?:[Ee]\d{2,3})+)(?!\d)""")
    private val SINGLE_REGEX = Regex("""(?<!\w)[Ss](\d{1,2})[._\-\s]*[Ee](\d{1,3})(?!\d)""")

    private val ENG_SEASON_FIRST_RANGE_REGEX = Regex(
        """(?<!\w)(?:season\b|s(?=\s*\d))[._\-\s]*(\d{1,2})[._\-\s]*(?:episode\b|ep\b|e(?=\s*\d)|[-_~\s])[._\-\s]*(\d{1,3})\s*[-_~]\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val ENG_SEASON_FIRST_SINGLE_REGEX = Regex(
        """(?<!\w)(?:season\b|s(?=\s*\d))[._\-\s]*(\d{1,2})[._\-\s]*(?:episode\b|ep\b|e(?=\s*\d)|[-_~])[._\-\s]*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val ENG_ROMAN_SEASON_SINGLE_REGEX = Regex(
        """(?<!\w)season\s+([IVXLCDM]+)[._\-\s]+(?:episode\b|ep\b|e(?=\s*\d))[._\-\s]*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val ENG_EPISODE_FIRST_SINGLE_REGEX = Regex(
        """(?<!\w)(?:episode\b|ep\b|e(?=\s*\d))[._\-\s]*(\d{1,3})[._\-\s]*(?:season\b|s(?=\s*\d))[._\-\s]*(\d{1,2})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val SHORT_RANGE_REGEX = Regex("""(?<!\d)(\d{1,2})[._\-\s]*[xXхХ][._\-\s]*(\d{1,3})\s*[-_~+&/]\s*(\d{1,3})(?!\d)""")
    private val SHORT_SINGLE_REGEX = Regex("""(?<!\d)(\d{1,2})[._\-\s]*[xXхХ][._\-\s]*(\d{1,3})(?!\d)""")
    private val PARTIAL_EP_RANGE_REGEX = Regex(
        """(?<!\w)(?:episode\b|ep\b|e(?=\s*\d)|ep|episode)[._-]?\s*(\d{1,4})\s*(?:[-_~]|_\s*ep?\s*)\s*(?:episode\b|ep\b|e(?=\s*\d)|ep|episode)?[._-]?\s*(\d{1,4})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val PARTIAL_EP_LIST_REGEX = Regex(
        """(?<!\w)(?:episode\b|ep\b|e(?=\s*\d)|ep)\s*(\d{1,3}(?:_\d{1,3})+)(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val PARTIAL_EP_SINGLE_REGEX =
        Regex("""(?<!\w)(?:episode\b|ep\b|e(?=\s*\d)|ep|episode)[._-]?\s*(\d{1,4})(?!\d)""", RegexOption.IGNORE_CASE)

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

        ENG_ROMAN_SEASON_SINGLE_REGEX.find(cleaned)?.let { match ->
            val seasonNum = ParserUtils.parseRomanNumeral(match.groupValues[1])
            if (seasonNum != null) {
                return MappingResult.Success(listOf(seasonNum), listOf(match.groupValues[2].toInt()))
            }
        }

        ENG_SEASON_FIRST_RANGE_REGEX.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        ENG_SEASON_FIRST_SINGLE_REGEX.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        ENG_EPISODE_FIRST_SINGLE_REGEX.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[2].toInt()), listOf(match.groupValues[1].toInt()))
        }

        SHORT_RANGE_REGEX.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        SHORT_SINGLE_REGEX.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        PARTIAL_EP_LIST_REGEX.find(cleaned)?.let { match ->
            val episodes = match.groupValues[1].split('_').map { it.toInt() }
            return MappingResult.Partial(episodes)
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
    companion object {
        private const val SEASON_WORDS = """(?:сезон\b|сезона\b|сезоны\b|сезонов\b|сс\b|сезон|сезона|сезоны|сезонов)"""
        private const val EP_WORDS = """(?:серия\b|серии\b|серий\b|сер\b|эпизод\b|эпизоды\b|эп\b|сериал\b|выпуск\b|выпуска\b|часть\b|части\b|фильм\b|фильма\b|\bч\b|\bч\.|серия|серии|сер|эпизод|эпизоды)"""
    }

    private val CYR_MULTI_SEASON = Regex(
        """(?U)(?<!\d)(\d{1,2})\s*-\s*(\d{1,2})\s*$SEASON_WORDS[()\s,._\-/:]*$EP_WORDS[()\s,._\-/:]*(\d{1,3})\s*[-_~]\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_MULTI_SEASON_SINGLE_EP = Regex(
        """(?U)(?<!\d)(\d{1,2})\s*-\s*(\d{1,2})\s*$SEASON_WORDS[()\s,._\-/:]*$EP_WORDS[()\s,._\-/:]*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_RANGE_REGEX =
        Regex("""(?<!\w)[Сс](\d{1,2})[._\-\s]*[ЕеЭэ](\d{1,3})(?:\s*[-_~еЕэЭ/\\+&]\s*)[ЕеЭэ]?(\d{1,3})(?!\d)""")
    private val CYR_LIST_REGEX = Regex("""(?<!\w)[Сс](\d{1,2})[._\-\s]*((?:[ЕеЭэ]\d{2,3})+)(?!\d)""")
    private val CYR_SINGLE_REGEX = Regex("""(?<!\w)[Сс](\d{1,2})[._\-\s]*[ЕеЭэ](\d{1,3})(?!\d)""")

    private val TEXT_SEASON_PACK_COLON_RANGE = Regex(
        """(?U)(?<!\d)(\d{1,2})[()\s,._\-/:]*$SEASON_WORDS[()\s,._\-/:]+(\d{1,3})\s*[-_~]\s*(\d{1,3})\s*(?:$EP_WORDS)?[()\s,._\-/:]+(?:из|/)\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val TEXT_SEASON_OUT_OF_RANGE = Regex(
        """(?U)(?<!\d)(\d{1,2})[()\s,._\-/:]*$SEASON_WORDS[()\s,._\-/:]+(\d{1,3})\s*[-_~]\s*(\d{1,3})\s*(?:из|/)\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val TEXT_SEASON_OUT_OF_SINGLE = Regex(
        """(?U)(?<!\d)(\d{1,2})[()\s,._\-/:]*$SEASON_WORDS[()\s,._\-/:]+(\d{1,3})\s*(?:из|/)\s*(\d{1,3})\s*(?:$EP_WORDS)?(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_SEASON_OUT_OF_RANGE = Regex(
        """(?U)(?<!\d\s*)(?<!\d)$SEASON_WORDS[()\s,._\-/:]+(\d{1,2})[()\s,._\-/:]+(\d{1,3})\s*[-_~]\s*(\d{1,3})\s*(?:из|/)\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_SEASON_OUT_OF_SINGLE = Regex(
        """(?U)(?<!\d\s*)(?<!\d)$SEASON_WORDS[()\s,._\-/:]+(\d{1,2})[()\s,._\-/:]+(\d{1,3})\s*(?:из|/)\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )

    private val CYR_EP_FIRST_NUM_FIRST = Regex(
        """(?U)(?<!\d)(\d{1,3})[()\s,._\-/:]*$EP_WORDS[()\s,._\-/:]*(?:из\s+)?(\d{1,2})(?:-?(?:го|ого|й|ой|ый|ий|я))?[()\s,._\-/:]*$SEASON_WORDS(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_EP_FIRST_WORD_FIRST = Regex(
        """(?U)(?<!\d)$EP_WORDS[()\s,._\-/:]*(\d{1,3})[()\s,._\-/:]*(?:из\s+)?(\d{1,2})(?:-?(?:го|ого|й|ой|ый|ий|я))?[()\s,._\-/:]*$SEASON_WORDS(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_EP_FIRST_WORD_FIRST_SEASON_FIRST = Regex(
        """(?U)(?<!\d)$EP_WORDS[()\s,._\-/:]*(\d{1,3})[()\s,._\-/:]*(?:из\s+)?$SEASON_WORDS[()\s,._\-/:]*(\d{1,2})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_EP_FIRST_NUM_FIRST_SEASON_FIRST = Regex(
        """(?U)(?<!\d)(\d{1,3})[()\s,._\-/:]*$EP_WORDS[()\s,._\-/:]*(?:из\s+)?$SEASON_WORDS[()\s,._\-/:]*(\d{1,2})(?!\d)""",
        RegexOption.IGNORE_CASE
    )

    private val CYR_SEASON_FIRST_RANGE = Regex(
        """(?U)(?<!\d\s*)(?<!\d)$SEASON_WORDS[()\s,._\-/:]*(\d{1,2})[()\s,._\-/:]*(?:$EP_WORDS)?[()\s,._\-/:]*(\d{1,3})\s*[-_~]\s*(\d{1,3})\s*(?:$EP_WORDS|\))?(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_SEASON_FIRST_SINGLE = Regex(
        """(?U)(?<!\d\s*)(?<!\d)$SEASON_WORDS[()\s,._\-/:]*(\d{1,2})[()\s,._\-/:]*$EP_WORDS[()\s,._\-/:]*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_ORDINAL = Regex(
        """(?U)(?<!\d)(\d{1,2})-(?:й|ый|ой|ий|я)\s*сезон[()\s,._\-/:]+(\d{1,3})-(?:я|ья|ая|ей|й)\s*серия(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val TEXT_RANGE = Regex(
        """(?U)(?<!\d)(\d{1,2})[()\s,._\-/:]*$SEASON_WORDS[()\s,._\-/:]*(?:$EP_WORDS)?[()\s,._\-/:]*(\d{1,3})\s*[-_~]\s*(\d{1,3})[()\s,._\-/:]*(?:$EP_WORDS)?(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val TEXT_SINGLE_1 = Regex(
        """(?U)(?<!\d)(\d{1,2})[()\s,._\-/:]*$SEASON_WORDS[()\s,._\-/:]*$EP_WORDS[()\s,._\-/:]*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val TEXT_SINGLE_NUM_BEFORE_EP = Regex(
        """(?U)(?<!\d)(\d{1,2})[()\s,._\-/:]*$SEASON_WORDS[()\s,._\-/:]*(\d{1,3})[()\s,._\-/:]*$EP_WORDS(?!\d)""",
        RegexOption.IGNORE_CASE
    )

    private val CYR_PARTIAL_SINGLE_BEFORE_EP = Regex(
        """(?U)(?<!\d)(\d{1,3})[()\s,._\-/:]*$EP_WORDS(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_PARTIAL_RANGE = Regex(
        """(?U)(?<!\d)$EP_WORDS[()\s,._\-/:]*(\d{1,3})\s*[-_~]\s*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_PARTIAL_RANGE_NUM_FIRST = Regex(
        """(?U)(?<!\d)(\d{1,3})\s*[-_~]\s*(\d{1,3})[()\s,._\-/:]*(?:серии\b|серий\b|сер\b|выпуски\b|эпизоды\b|части\b)(?!\d)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_TITLE_SEASON_EP_SINGLE = Regex(
        """(?U)^.*?\b(\d{1,2})\s*[()\s,._\-/:]*\((\d{1,3})\s*(?:серия|эпизод|выпуск)\)""",
        RegexOption.IGNORE_CASE
    )
    private val CYR_PARTIAL_SINGLE = Regex(
        """(?U)(?<!\d)$EP_WORDS[()\s,._\-/:]*(\d{1,3})(?!\d)""",
        RegexOption.IGNORE_CASE
    )

    override fun match(filename: String): MappingResult {
        val cleaned = GarbageCollector.cleanBasic(filename)

        CYR_TITLE_SEASON_EP_SINGLE.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        TEXT_SEASON_PACK_COLON_RANGE.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        TEXT_SEASON_OUT_OF_RANGE.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        TEXT_SEASON_OUT_OF_SINGLE.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        CYR_SEASON_OUT_OF_RANGE.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        CYR_SEASON_OUT_OF_SINGLE.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
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

        CYR_EP_FIRST_WORD_FIRST_SEASON_FIRST.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[2].toInt()), listOf(match.groupValues[1].toInt()))
        }

        CYR_EP_FIRST_NUM_FIRST_SEASON_FIRST.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[2].toInt()), listOf(match.groupValues[1].toInt()))
        }

        CYR_EP_FIRST_NUM_FIRST.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[2].toInt()), listOf(match.groupValues[1].toInt()))
        }

        CYR_EP_FIRST_WORD_FIRST.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[2].toInt()), listOf(match.groupValues[1].toInt()))
        }

        CYR_SEASON_FIRST_RANGE.find(cleaned)?.let { match ->
            val seasons = listOf(match.groupValues[1].toInt())
            val episodes = ParserUtils.parseRange(match.groupValues[2], match.groupValues[3])
            return MappingResult.Success(seasons, episodes)
        }

        CYR_SEASON_FIRST_SINGLE.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        CYR_ORDINAL.find(cleaned)?.let { match ->
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

        TEXT_SINGLE_NUM_BEFORE_EP.find(cleaned)?.let { match ->
            return MappingResult.Success(listOf(match.groupValues[1].toInt()), listOf(match.groupValues[2].toInt()))
        }

        CYR_PARTIAL_RANGE_NUM_FIRST.find(cleaned)?.let { match ->
            return MappingResult.Partial(ParserUtils.parseRange(match.groupValues[1], match.groupValues[2]))
        }

        CYR_PARTIAL_SINGLE_BEFORE_EP.find(cleaned)?.let { match ->
            return MappingResult.Partial(listOf(match.groupValues[1].toInt()))
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
    private val ANIME_RANGE_REGEX = Regex("""(?<!\d)(\d{1,4})\s*[-_~]\s*(\d{1,4})(?!\d)""")
    private val ANIME_SINGLE_REGEX = Regex("""\s+-\s+(\d{1,4})(?!\d)""")
    private val NUMERIC_3_4_REGEX = Regex("""(?<!\d)(\d{1,2})(\d{2})(?!\d)""")
    private val LEADING_EP_REGEX = Regex("""^(?:\[[^\]]+\]\s*)?(\d{1,3})\b""")
    private val ALL_NUMBERS_REGEX = Regex("""\b\d{1,4}\b""")

    override fun match(filename: String): MappingResult {
        val heavyCleaned = GarbageCollector.cleanHeavy(filename)

        ANIME_RANGE_REGEX.find(heavyCleaned)?.let { match ->
            val episodes = ParserUtils.parseRange(match.groupValues[1], match.groupValues[2])
            if (episodes.isNotEmpty()) {
                return MappingResult.Partial(
                    episodes = episodes,
                    isAbsolute = true
                )
            }
        }

        ANIME_SINGLE_REGEX.find(heavyCleaned)?.let { match ->
            return MappingResult.Partial(
                episodes = listOf(match.groupValues[1].toInt()),
                isAbsolute = true
            )
        }

        val leadingEp = LEADING_EP_REGEX.find(heavyCleaned)
        if (leadingEp != null) {
            val ep = leadingEp.groupValues[1].toInt()
            if (ep in 1..1500) {
                return MappingResult.Partial(listOf(ep), isAbsolute = true)
            }
        }

        NUMERIC_3_4_REGEX.find(heavyCleaned)?.let { match ->
            val season = match.groupValues[1].toInt()
            val episode = match.groupValues[2].toInt()
            if (season in 1..25 && episode in 1..100) {
                return MappingResult.Success(listOf(season), listOf(episode))
            }
        }

        val allNumbers = ALL_NUMBERS_REGEX.findAll(heavyCleaned).map { it.value.toInt() }.toList()
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
    companion object {
        private val SPECIALS_REGEX = Regex("""(?i)\b(?:specials?|extras?|s00|сезон\s*0)\b""")
        private val S_RANGE_REGEX = Regex("""(?i)\bS(\d{1,2})\s*[-_~]\s*S?(\d{1,2})\b""")
        private val ROOT_SEASON_RANGE_REGEX = Regex("""(?U)\b(?:[Ss]easons?|[Сс]езоны?)\s*(\d{1,2})\s*[-_~]\s*(\d{1,2})\b""", RegexOption.IGNORE_CASE)
        private val CYR_PRE_RANGE_REGEX = Regex("""(?U)\b(\d{1,2})\s*[-_~]\s*(\d{1,2})\s*(?:[Ss]easons?|[Сс]езоны?|[Сс]езона?)\b""", RegexOption.IGNORE_CASE)
        private val SINGLE_S_REGEX = Regex("""(?i)(?<!\w)S(\d{1,2})(?!\d|\w)""")
        private val ROOT_SINGLE_SEASON_REGEX = Regex("""(?U)\b(?:[Ss]eason|[Сс]езон)\s*(\d{1,2})\b""")
        private val PRE_SINGLE_SEASON_REGEX = Regex("""(?U)\b(\d{1,2})\s*(?:[Ss]eason|[Сс]езон)\b""", RegexOption.IGNORE_CASE)
        private val MINISERIES_REGEX = Regex("""(?i)\b(?:miniseries|mini-series)\b""")
        private val COMPLETE_SERIES_REGEX = Regex("""(?i)\b(?:complete\s*series|complete)\b""")
        private val HAS_SEASONS_REGEX = Regex("""(?i)\b(?:seasons?|сезоны?)\b""")
        private val HAS_S_REGEX = Regex("""(?i)\bS\d+\b""")
        private val LONG_RUNNING_ANIME_REGEX = Regex("""(?i)\b(?:naruto|bleach|one\s*piece|dragon\s*ball|gintama|detective\s*conan|attack\s*on\s*titan)\b""")
        private val DISC_OR_PART_REGEX = Regex("""(?i)\b(?:disc|disk|cd|part|часть|диск)\b""")
        private val TRAIL_NUM_REGEX = Regex("""(?<!\d)(\d{1,2})\s*(?:\([12]\d{3}\))?\s*$""")
    }

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

        if (SPECIALS_REGEX.containsMatchIn(text)) {
            return listOf(0)
        }

        val sRange = S_RANGE_REGEX.find(text)
        if (sRange != null) {
            return ParserUtils.parseRange(sRange.groupValues[1], sRange.groupValues[2])
        }

        ROOT_SEASON_RANGE_REGEX.find(text)?.let { match ->
            return ParserUtils.parseRange(match.groupValues[1], match.groupValues[2])
        }

        CYR_PRE_RANGE_REGEX.find(text)?.let { match ->
            return ParserUtils.parseRange(match.groupValues[1], match.groupValues[2])
        }

        val singleS = SINGLE_S_REGEX.find(text)
        if (singleS != null) {
            return listOf(singleS.groupValues[1].toInt())
        }

        val rootSingleSeason = ROOT_SINGLE_SEASON_REGEX.find(text)
        if (rootSingleSeason != null) {
            return listOf(rootSingleSeason.groupValues[1].toInt())
        }

        PRE_SINGLE_SEASON_REGEX.find(text)?.let { match ->
            return listOf(match.groupValues[1].toInt())
        }

        if (MINISERIES_REGEX.containsMatchIn(text) ||
            (COMPLETE_SERIES_REGEX.containsMatchIn(text) &&
             !HAS_SEASONS_REGEX.containsMatchIn(text) &&
             !HAS_S_REGEX.containsMatchIn(text) &&
             !LONG_RUNNING_ANIME_REGEX.containsMatchIn(text))
        ) {
            return listOf(1)
        }

        val isDiscOrPart = DISC_OR_PART_REGEX.containsMatchIn(text)
        if (!isDiscOrPart) {
            val trailNum = TRAIL_NUM_REGEX.find(text)
            if (trailNum != null) {
                val n = trailNum.groupValues[1].toInt()
                if (n in 1..50) {
                    return listOf(n)
                }
            }
        }

        return emptyList()
    }

    fun parseBatch(season: String, episodes: List<String>): Map<String, MappingResult> {
        val rootSeasons = extractSeasonsContext(season)
        val batchResults = mutableMapOf<String, MappingResult>()

        for (filePath in episodes) {
            var fileResult: MappingResult = MappingResult.Failed("Initial state")

            val fileName = filePath.substringAfterLast('/').substringAfterLast('\\')
            val parentPath = when {
                filePath.contains('/') -> filePath.substringBeforeLast('/')
                filePath.contains('\\') -> filePath.substringBeforeLast('\\')
                else -> ""
            }

            // 1. If single-level string with fraction or full title, try matching whole filePath
            if (!filePath.contains('\\') && !filePath.contains(".mkv") && !filePath.contains(".mp4") && !filePath.contains(".avi")) {
                for (provider in listOf(providers[0], providers[1])) {
                    val wholeMatch = provider.match(filePath)
                    if (wholeMatch is MappingResult.Success) {
                        fileResult = wholeMatch
                        break
                    }
                }
            }

            if (fileResult is MappingResult.Success) {
                batchResults[filePath] = fileResult
                continue
            }

            // 2. Otherwise evaluate filename and parent hierarchy
            var pathSeasons = emptyList<Int>()
            if (parentPath.isNotEmpty()) {
                val segments = parentPath.split('/', '\\')
                for (seg in segments.reversed()) {
                    val s = extractSeasonsContext(seg)
                    if (s.isNotEmpty() && !s.contains(0)) {
                        pathSeasons = s
                        break
                    }
                }
                if (pathSeasons.isEmpty()) {
                    for (seg in segments.reversed()) {
                        val s = extractSeasonsContext(seg)
                        if (s.isNotEmpty()) {
                            pathSeasons = s
                            break
                        }
                    }
                }
            }

            fileResult = MappingResult.Failed("Initial state")
            var matchedByExplicitProvider = false
            for (provider in listOf(providers[0], providers[1])) {
                val match = provider.match(fileName)
                if (match !is MappingResult.Failed) {
                    fileResult = match
                    matchedByExplicitProvider = true
                    break
                }
            }

            if (!matchedByExplicitProvider) {
                fileResult = providers[2].match(fileName)
            }

            if (!matchedByExplicitProvider && pathSeasons.isNotEmpty() && fileResult is MappingResult.Success && fileResult.seasons != pathSeasons) {
                val rawEp = (fileResult.seasons.first() * 100) + fileResult.episodes.first()
                fileResult = MappingResult.Success(pathSeasons, listOf(rawEp), isAbsolute = true)
            }

            if (fileResult !is MappingResult.Success && filePath != fileName) {
                for (provider in providers) {
                    val fullMatch = provider.match(filePath)
                    if (fullMatch is MappingResult.Success) {
                        fileResult = fullMatch
                        break
                    }
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
                    if (pathSeasons.size == 1 && fileResult.episodes.size == 1) {
                        val parentS = pathSeasons.first()
                        val num = fileResult.episodes.first()
                        if (num in 100..999 && (num / 100) == parentS && parentS > 0) {
                            MappingResult.Success(pathSeasons, listOf(num % 100), isAbsolute = false)
                        } else {
                            MappingResult.Success(pathSeasons, fileResult.episodes, fileResult.isAbsolute)
                        }
                    } else {
                        MappingResult.Success(pathSeasons, fileResult.episodes, fileResult.isAbsolute)
                    }
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
