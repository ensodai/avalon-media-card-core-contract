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

    private fun formatString(pattern: String, args: Array<out Any>): String {
        var result = pattern
        // Replace positional placeholders {0}, {1}, etc.
        for (i in args.indices) {
            result = result.replace("{$i}", args[i].toString())
        }
        // Replace %1$s, %2$s, %1$d, etc.
        for (i in args.indices) {
            val num = i + 1
            result = result.replace("%$num\$s", args[i].toString())
            result = result.replace("%$num\$d", args[i].toString())
        }
        // Replace sequential %s / %d
        for (arg in args) {
            if (result.contains("%s")) {
                result = result.replaceFirst("%s", arg.toString())
            } else if (result.contains("%d")) {
                result = result.replaceFirst("%d", arg.toString())
            }
        }
        return result
    }
}

/**
 * Fallback empty i18n provider when no translation files are present in plugin.
 */
object EmptyPluginI18n : PluginI18n {
    override suspend fun t(key: String, vararg args: Any): String = key
    override fun tForLocale(locale: String, key: String, vararg args: Any): String = key
}
