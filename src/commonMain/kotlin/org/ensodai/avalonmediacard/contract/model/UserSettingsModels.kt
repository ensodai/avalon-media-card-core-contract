package org.ensodai.avalonmediacard.contract.model

import kotlinx.serialization.Serializable

@Serializable
enum class TitleDisplayMode {
    LOCALIZED,
    ORIGINAL
}

@Serializable
data class UserSettingsDto(
    val uiLocale: String = "ru",
    val posterLanguage: String? = null,
    val titleMode: TitleDisplayMode = TitleDisplayMode.LOCALIZED,
    val titleLanguage: String? = null,
    val overviewLanguage: String? = null
)

fun UserSettingsDto?.resolveTargetLanguage(): String {
    val posterLang = this?.posterLanguage?.takeIf { it.isNotBlank() }
    if (posterLang != null) {
        return if (posterLang.equals("original", ignoreCase = true)) "en" else posterLang
    }
    return this?.uiLocale?.takeIf { it.isNotBlank() } ?: "ru"
}
