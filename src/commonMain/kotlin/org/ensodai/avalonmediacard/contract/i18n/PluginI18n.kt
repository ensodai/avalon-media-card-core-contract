package org.ensodai.avalonmediacard.contract.i18n

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * CoroutineContext element storing the active client/user locale code (e.g. "ru", "en", "de").
 */
class PluginLocaleElement(val locale: String) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<PluginLocaleElement>
    override val key: CoroutineContext.Key<*> = Key
}

/**
 * Helper to fetch the current user/request locale from the active CoroutineContext.
 * Defaults to "ru" if not explicitly specified in context.
 */
suspend fun currentPluginLocale(): String {
    return coroutineContext[PluginLocaleElement]?.locale?.takeIf { it.isNotBlank() } ?: "ru"
}

/**
 * Universal internationalization interface for Avalon plugins.
 */
interface PluginI18n {
    /**
     * Resolves localized string by key for the active user locale in current coroutine context.
     */
    suspend fun t(key: String, vararg args: Any): String

    /**
     * Resolves localized string by key for an explicit locale code.
     */
    fun tForLocale(locale: String, key: String, vararg args: Any): String
}

/**
 * Standard in-memory translation provider loaded from JSON or properties.
 */
class MapPluginI18n(
    private val translations: Map<String, Map<String, String>>,
    private val defaultLocale: String = "en"
) : PluginI18n {

    override suspend fun t(key: String, vararg args: Any): String {
        val locale = currentPluginLocale()
        return tForLocale(locale, key, *args)
    }

    override fun tForLocale(locale: String, key: String, vararg args: Any): String {
        val normalizedLocale = normalizeLocale(locale)
        val localeMap = translations[normalizedLocale]
            ?: translations[locale]
            ?: translations[defaultLocale]
            ?: translations.values.firstOrNull()

        val rawPattern = localeMap?.get(key)
            ?: translations[defaultLocale]?.get(key)
            ?: key

        if (args.isEmpty()) return rawPattern

        return formatString(rawPattern, args)
    }

    private fun normalizeLocale(code: String): String {
        val trimmed = code.trim().lowercase()
        return if (trimmed.contains("-") || trimmed.contains("_")) {
            trimmed.substringBefore("-").substringBefore("_")
        } else {
            trimmed
        }
    }

    companion object {
        private val PLACEHOLDER_REGEX = Regex("""\{(\d+)\}|%(\d+)\$[sd]|%[sd]""")
    }

    private fun formatString(pattern: String, args: Array<out Any>): String {
        var sequentialIndex = 0
        return PLACEHOLDER_REGEX.replace(pattern) { match ->
            val positional = match.groups[1]?.value?.toIntOrNull()
            val numbered = match.groups[2]?.value?.toIntOrNull()
            when {
                positional != null -> args.getOrNull(positional)?.toString() ?: match.value
                numbered != null -> args.getOrNull(numbered - 1)?.toString() ?: match.value
                else -> {
                    val idx = sequentialIndex++
                    args.getOrNull(idx)?.toString() ?: match.value
                }
            }
        }
    }
}

/**
 * Fallback empty i18n provider when no translation files are present in plugin.
 */
object EmptyPluginI18n : PluginI18n {
    override suspend fun t(key: String, vararg args: Any): String = key
    override fun tForLocale(locale: String, key: String, vararg args: Any): String = key
}
