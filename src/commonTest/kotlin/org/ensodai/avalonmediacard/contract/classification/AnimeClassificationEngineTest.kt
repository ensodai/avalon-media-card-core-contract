package org.ensodai.avalonmediacard.contract.classification

import kotlin.test.Test
import kotlin.test.assertEquals

class AnimeClassificationEngineTest {

    private data class TestCase(
        val name: String,
        val title: String,
        val originalTitle: String?,
        val genres: List<Int>,
        val keywords: List<String>,
        val studios: List<String>,
        val country: String,
        val lang: String,
        val networks: List<String>?,
        val expectedIsAnime: Boolean,
        val expectedSubType: AnimeSubType
    )

    @Test
    fun testComprehensiveMatrixValidation50Titles() {
        val testCases = listOf(
            // Category 1: Classic & Modern Japanese Anime
            TestCase("Spirited Away", "Spirited Away", "千と千尋の神隠し", listOf(16, 14, 10751), listOf("anime", "spirits"), listOf("Studio Ghibli"), "JP", "ja", null, true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Your Name", "Your Name.", "君の名は。", listOf(16, 10749, 18), listOf("anime", "body exchange"), listOf("CoMix Wave Films"), "JP", "ja", null, true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Attack on Titan", "Attack on Titan", "進撃の巨人", listOf(16, 10759, 10765), listOf("anime", "dystopia"), listOf("Wit Studio", "MAPPA"), "JP", "ja", listOf("MBS"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Frieren", "Frieren: Beyond Journey's End", "葬送のフリーレン", listOf(16, 10759, 10765), listOf("anime", "elf"), listOf("Madhouse"), "JP", "ja", listOf("Nippon Television"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Cowboy Bebop", "Cowboy Bebop", "Cowboy Bebop", listOf(16, 10759, 10765), listOf("anime", "space western"), listOf("Sunrise"), "JP", "ja", listOf("TV Tokyo"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Monster", "Monster", "Monster", listOf(16, 18, 9648), listOf("anime", "serial killer"), listOf("Madhouse"), "JP", "ja", listOf("Nippon Television"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Black Lagoon", "Black Lagoon", "Black Lagoon", listOf(16, 10759), listOf("anime", "mercenary"), listOf("Madhouse"), "JP", "ja", listOf("Chiba TV"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Erased", "Erased", "Boku dake ga Inai Machi", listOf(16, 9648, 18), listOf("anime", "time travel"), listOf("A-1 Pictures"), "JP", "ja", listOf("Fuji TV"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Overlord", "Overlord", "Overlord", listOf(16, 10759, 10765), listOf("anime", "isekai"), listOf("Madhouse"), "JP", "ja", listOf("AT-X"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Made in Abyss", "Made in Abyss", "Made in Abyss", listOf(16, 10759, 10765), listOf("anime", "abyss"), listOf("Kinema Citrus"), "JP", "ja", listOf("AT-X"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Spy x Family", "SPY x FAMILY", "SPY×FAMILY", listOf(16, 35, 10759), listOf("anime", "spy"), listOf("Wit Studio", "CloverWorks"), "JP", "ja", listOf("TV Tokyo"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Dr. STONE", "Dr. STONE", "Dr. STONE", listOf(16, 10759, 10765), listOf("anime", "science"), listOf("TMS Entertainment"), "JP", "ja", listOf("Tokyo MX"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Neon Genesis Evangelion", "Neon Genesis Evangelion", "新世紀エヴァンゲリオン", listOf(16, 10765, 18), listOf("anime", "mecha"), listOf("Gainax", "Tatsunoko Production"), "JP", "ja", listOf("TV Tokyo"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Fullmetal Alchemist", "Fullmetal Alchemist: Brotherhood", "鋼の錬金術師", listOf(16, 10759, 10765), listOf("anime", "alchemy"), listOf("Bones"), "JP", "ja", listOf("MBS"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Steins;Gate", "Steins;Gate", "Steins;Gate", listOf(16, 10765, 18), listOf("anime", "time travel"), listOf("White Fox"), "JP", "ja", listOf("Tokyo MX"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Hunter x Hunter", "Hunter x Hunter", "Hunter x Hunter", listOf(16, 10759, 10765), listOf("anime", "hunter"), listOf("Madhouse"), "JP", "ja", listOf("Nippon Television"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("One Punch Man", "One Punch Man", "ワンパンマン", listOf(16, 10759, 35), listOf("anime", "superhero"), listOf("Madhouse", "J.C.Staff"), "JP", "ja", listOf("TV Tokyo"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Mob Psycho 100", "Mob Psycho 100", "モブサイコ100", listOf(16, 10759, 35), listOf("anime", "psychic"), listOf("Bones"), "JP", "ja", listOf("Tokyo MX"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Violet Evergarden", "Violet Evergarden", "ヴァイオレット・エヴァーガーデン", listOf(16, 18, 10765), listOf("anime", "war"), listOf("Kyoto Animation"), "JP", "ja", listOf("Tokyo MX"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Vinland Saga", "Vinland Saga", "ヴィンランド・サガ", listOf(16, 10759, 18), listOf("anime", "viking"), listOf("Wit Studio", "MAPPA"), "JP", "ja", listOf("NHK"), true, AnimeSubType.JAPANESE_ANIME),

            // Category 2: Edge Case A & B (Romaji / English Original Titles)
            TestCase("Demon Slayer", "Demon Slayer: Kimetsu no Yaiba", "Kimetsu no Yaiba", listOf(16, 10759, 10765), listOf("anime", "demons"), listOf("ufotable", "Aniplex"), "JP", "ja", listOf("Tokyo MX"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Attack on Titan (Romaji)", "Attack on Titan", "Shingeki no Kyojin", listOf(16, 10759), listOf("anime"), listOf("MAPPA"), "JP", "ja", listOf("NHK"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Jujutsu Kaisen", "Jujutsu Kaisen", "Jujutsu Kaisen", listOf(16, 10759, 10765), listOf("anime", "curse"), listOf("MAPPA"), "JP", "ja", listOf("MBS"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("My Hero Academia", "My Hero Academia", "Boku no Hero Academia", listOf(16, 10759, 10765), listOf("anime", "superhero"), listOf("Bones"), "JP", "ja", listOf("YTV"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Death Note", "Death Note", "Death Note", listOf(16, 9648, 18), listOf("detective"), listOf("Madhouse"), "JP", "ja", listOf("Nippon Television"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Bleach", "Bleach", "Bleach", listOf(16, 10759, 10765), listOf("anime", "soul reaper"), listOf("Pierrot"), "JP", "ja", listOf("TV Tokyo"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Cyberpunk: Edgerunners", "Cyberpunk: Edgerunners", "Cyberpunk: Edgerunners", listOf(16, 10759, 10765), listOf("anime", "cyberpunk"), listOf("Trigger", "CD Projekt RED"), "JP", "ja", listOf("Netflix"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Naruto", "Naruto", "Naruto", listOf(16, 10759, 10765), listOf("anime", "ninja"), listOf("Pierrot"), "JP", "ja", listOf("TV Tokyo"), true, AnimeSubType.JAPANESE_ANIME),

            // Category 3: Edge Case C (Live Action Japanese Cinema - MUST FAIL)
            TestCase("Seven Samurai", "Seven Samurai", "七人の侍", listOf(28, 18), listOf("samurai", "feudal japan"), listOf("Toho"), "JP", "ja", null, false, AnimeSubType.NOT_ANIME),
            TestCase("Godzilla Minus One", "Godzilla Minus One", "ゴジラ-1.0", listOf(28, 878, 27), listOf("kaiju", "godzilla"), listOf("Toho", "Robot Communications"), "JP", "ja", null, false, AnimeSubType.NOT_ANIME),
            TestCase("Drive My Car", "Drive My Car", "ドライブ・マイ・カー", listOf(18), listOf("grief", "theater"), listOf("Bitters End"), "JP", "ja", null, false, AnimeSubType.NOT_ANIME),
            TestCase("Shogun", "Shōgun", "Shōgun", listOf(18, 10768), listOf("samurai", "historical fiction"), listOf("FX Productions"), "US", "en", listOf("FX"), false, AnimeSubType.NOT_ANIME),
            TestCase("Alice in Borderland", "Alice in Borderland", "今際の国のアリス", listOf(18, 9648, 10759), listOf("survival game", "dystopia"), listOf("Robot Communications"), "JP", "ja", listOf("Netflix"), false, AnimeSubType.NOT_ANIME),
            TestCase("Tokyo Vice", "Tokyo Vice", "Tokyo Vice", listOf(18, 80), listOf("yakuza", "journalism"), listOf("Endeavor Content"), "US", "en", listOf("Max"), false, AnimeSubType.NOT_ANIME),

            // Category 4: Edge Case D (Live Action Manga Adaptations - MUST FAIL)
            TestCase("One Piece (Netflix)", "One Piece", "One Piece", listOf(10759, 10765), listOf("based on manga", "pirate", "live action"), listOf("Tomorrow Studios", "Netflix"), "US", "en", listOf("Netflix"), false, AnimeSubType.NOT_ANIME),
            TestCase("Rurouni Kenshin", "Rurouni Kenshin", "るろうに剣心", listOf(28, 12, 18), listOf("based on manga", "samurai", "live action"), listOf("Warner Bros. Japan"), "JP", "ja", null, false, AnimeSubType.NOT_ANIME),
            TestCase("Alita: Battle Angel", "Alita: Battle Angel", "Alita: Battle Angel", listOf(28, 878, 12), listOf("based on manga", "cyborg", "live action"), listOf("Lightstorm Entertainment", "20th Century Fox"), "US", "en", null, false, AnimeSubType.NOT_ANIME),
            TestCase("Death Note (2017)", "Death Note", "Death Note", listOf(14, 27, 9648), listOf("based on manga", "shinigami"), listOf("Vertigo Entertainment"), "US", "en", null, false, AnimeSubType.NOT_ANIME),
            TestCase("Ghost in the Shell", "Ghost in the Shell", "Ghost in the Shell", listOf(878, 28, 80), listOf("based on manga", "cyberpunk"), listOf("DreamWorks Pictures"), "US", "en", null, false, AnimeSubType.NOT_ANIME),

            // Category 5: Edge Case E (Chinese Donghua)
            TestCase("Link Click", "Link Click", "时光代理人", listOf(16, 9648, 10765), listOf("time travel", "super power", "donghua"), listOf("bilibili", "Haoliners Animation League"), "CN", "zh", listOf("bilibili"), true, AnimeSubType.CHINESE_DONGHUA),
            TestCase("The King's Avatar", "The King's Avatar", "全职高手", listOf(16, 10759), listOf("esports", "donghua"), listOf("Tencent Video", "G.CMay Animation & Film"), "CN", "zh", listOf("Tencent Video"), true, AnimeSubType.CHINESE_DONGHUA),
            TestCase("Scissor Seven", "Scissor Seven", "刺客伍六七", listOf(16, 35, 10759), listOf("assassin", "donghua"), listOf("Sharefun Studio", "AHA Entertainment"), "CN", "zh", listOf("bilibili"), true, AnimeSubType.CHINESE_DONGHUA),
            TestCase("Heaven Official's Blessing", "Heaven Official's Blessing", "天官赐福", listOf(16, 10759, 10765), listOf("xianxia", "donghua"), listOf("bilibili", "Haoliners Animation League"), "CN", "zh", listOf("bilibili"), true, AnimeSubType.CHINESE_DONGHUA),
            TestCase("Soul Land", "Soul Land", "斗罗大陆", listOf(16, 10759, 10765), listOf("cultivation", "donghua"), listOf("Tencent Video", "Sparkly Key Animation"), "CN", "zh", listOf("Tencent Video"), true, AnimeSubType.CHINESE_DONGHUA),

            // Category 6: Edge Case E (Korean Aeni & Webtoon Adaptations)
            TestCase("Solo Leveling", "Solo Leveling", "俺だけレベルアップな件", listOf(16, 10759, 10765), listOf("based on webtoon", "hunters"), listOf("A-1 Pictures", "D&C Media"), "JP", "ja", listOf("Tokyo MX"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Tower of God", "Tower of God", "神之塔 -Tower of God-", listOf(16, 10759, 10765), listOf("based on webtoon"), listOf("Telecom Animation Film"), "JP", "ja", listOf("Tokyo MX"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("The God of High School", "The God of High School", "The God of High School", listOf(16, 10759, 10765), listOf("based on webtoon", "martial arts"), listOf("MAPPA"), "JP", "ja", listOf("AT-X"), true, AnimeSubType.JAPANESE_ANIME),
            TestCase("Lookism", "Lookism", "外見至上主義", listOf(16, 18, 35), listOf("based on webtoon", "bullying"), listOf("Studio Mir"), "KR", "ko", listOf("Netflix"), true, AnimeSubType.KOREAN_AENI),

            // Category 7: Edge Case F (Western Anime-Style / Co-Productions)
            TestCase("Avatar: The Last Airbender", "Avatar: The Last Airbender", "Avatar: The Last Airbender", listOf(16, 10759, 10765), listOf("elemental magic", "anime inspired"), listOf("Nickelodeon Animation Studios"), "US", "en", listOf("Nickelodeon"), true, AnimeSubType.ANIME_INSPIRED),
            TestCase("Castlevania", "Castlevania", "Castlevania", listOf(16, 10759, 10765), listOf("vampire", "anime inspired"), listOf("Powerhouse Animation Studios"), "US", "en", listOf("Netflix"), true, AnimeSubType.ANIME_INSPIRED),
            TestCase("Arcane", "Arcane", "Arcane", listOf(16, 10759, 10765), listOf("steampunk", "based on video game", "anime inspired"), listOf("Fortiche Production", "Riot Games"), "US", "en", listOf("Netflix"), true, AnimeSubType.ANIME_INSPIRED),
            TestCase("Blood of Zeus", "Blood of Zeus", "Blood of Zeus", listOf(16, 10759, 10765), listOf("greek mythology", "anime inspired"), listOf("Powerhouse Animation Studios"), "US", "en", listOf("Netflix"), true, AnimeSubType.ANIME_INSPIRED),
            TestCase("RWBY", "RWBY", "RWBY", listOf(16, 10759, 10765), listOf("huntress", "anime inspired"), listOf("Rooster Teeth"), "US", "en", listOf("Rooster Teeth"), true, AnimeSubType.ANIME_INSPIRED),
            TestCase("Dota: Dragon's Blood", "Dota: Dragon's Blood", "Dota: Dragon's Blood", listOf(16, 10759, 10765), listOf("based on video game", "anime inspired"), listOf("Studio Mir"), "US", "en", listOf("Netflix"), true, AnimeSubType.ANIME_INSPIRED),
            TestCase("The Witcher: Nightmare of the Wolf", "The Witcher: Nightmare of the Wolf", "The Witcher: Nightmare of the Wolf", listOf(16, 28, 14), listOf("witcher", "anime inspired"), listOf("Studio Mir", "Netflix"), "US", "en", null, true, AnimeSubType.ANIME_INSPIRED),
            TestCase("Scott Pilgrim Takes Off", "Scott Pilgrim Takes Off", "Scott Pilgrim Takes Off", listOf(16, 35, 10759), listOf("based on comic", "anime inspired"), listOf("Science SARU", "Universal Content Productions"), "US", "en", listOf("Netflix"), true, AnimeSubType.ANIME_INSPIRED),

            // Category 8: Standard Western Animation (MUST FAIL anime classification)
            TestCase("SpongeBob SquarePants", "SpongeBob SquarePants", "SpongeBob SquarePants", listOf(16, 35, 10751), listOf("sea sponge", "underwater", "cartoon"), listOf("Nickelodeon Animation Studios"), "US", "en", listOf("Nickelodeon"), false, AnimeSubType.NOT_ANIME),
            TestCase("Toy Story", "Toy Story", "Toy Story", listOf(16, 12, 10751, 35), listOf("living toys", "cgi animation"), listOf("Pixar", "Walt Disney Pictures"), "US", "en", null, false, AnimeSubType.NOT_ANIME),
            TestCase("The Simpsons", "The Simpsons", "The Simpsons", listOf(16, 35), listOf("sitcom", "satire", "cartoon"), listOf("Gracie Films", "20th Television"), "US", "en", listOf("FOX"), false, AnimeSubType.NOT_ANIME),
            TestCase("Rick and Morty", "Rick and Morty", "Rick and Morty", listOf(16, 35, 10765), listOf("mad scientist", "multiverse", "cartoon"), listOf("Williams Street"), "US", "en", listOf("Adult Swim"), false, AnimeSubType.NOT_ANIME),
            TestCase("BoJack Horseman", "BoJack Horseman", "BoJack Horseman", listOf(16, 35, 18), listOf("hollywood", "depression", "cartoon"), listOf("Tornante Company"), "US", "en", listOf("Netflix"), false, AnimeSubType.NOT_ANIME)
        )

        for (tc in testCases) {
            val result = AnimeClassificationEngine.analyze(
                genres = tc.genres,
                keywords = tc.keywords,
                productionCompanies = tc.studios,
                productionCountries = listOf(tc.country),
                originalLanguage = tc.lang,
                networks = tc.networks ?: emptyList(),
                originalTitle = tc.originalTitle,
                title = tc.title
            )

            assertEquals(
                expected = tc.expectedIsAnime,
                actual = result.isAnime,
                message = "Failed isAnime match for '${tc.name}': result=$result"
            )
            assertEquals(
                expected = tc.expectedSubType,
                actual = result.subType,
                message = "Failed subType match for '${tc.name}': result=$result"
            )
        }
    }
}
