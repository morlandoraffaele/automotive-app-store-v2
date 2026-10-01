package com.automotive.appstore.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale as JavaLocale

/**
 * Resolves a [StringKey] for a [Locale], interpolating `{placeholder}` tokens.
 *
 * Port of `createTranslator` / `formatDate` from the web app's `lib/i18n.ts`. English is the
 * fallback dictionary, mirroring the `dict[key] ?? en[key]` lookup there.
 */
class Translator(val locale: Locale) {

    private val javaLocale: JavaLocale = when (locale) {
        Locale.EN -> JavaLocale.US
        Locale.AR -> JavaLocale.forLanguageTag("ar")
    }

    private val dictionary: Map<StringKey, String> = when (locale) {
        Locale.EN -> StringsEn.values
        Locale.AR -> StringsAr.values
    }

    /**
     * Resolves a key, substituting `{name}` placeholders.
     *
     * Numbers are formatted for the active locale, mirroring the `Intl.NumberFormat` call in
     * the web `createTranslator`.
     */
    fun t(key: StringKey, vararg vars: Pair<String, Any>): String {
        val template = dictionary[key] ?: StringsEn.values.getValue(key)
        if (vars.isEmpty()) return template
        return vars.fold(template) { text, (name, value) ->
            val formatted = when (value) {
                is Int -> String.format(javaLocale, "%,d", value)
                is Double -> String.format(javaLocale, "%,.1f", value)
                else -> value.toString()
            }
            text.replace("{$name}", formatted)
        }
    }

    /** `en` / `ar` tag, for `LocaleList` and the web app's `LOCALE_META` parity. */
    val languageTag: String
        get() = when (locale) {
            Locale.EN -> "en"
            Locale.AR -> "ar"
        }

    /** `true` for right-to-left locales, matching `LOCALE_META[locale].dir` on the web. */
    val isRtl: Boolean
        get() = when (locale) {
            Locale.EN -> false
            Locale.AR -> true
        }

    /** Human-readable name shown in the settings language picker, for any [target] locale. */
    fun displayNameFor(target: Locale): String = when (target) {
        Locale.EN -> "English"
        Locale.AR -> "العربية"
    }

    private val dateFormatter: DateTimeFormatter
        get() = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(javaLocale)

    /** Formats an ISO `yyyy-MM-dd` date for display, e.g. `Sep 22, 2026`. */
    fun formatDate(isoDate: String): String = runCatching {
        dateFormatter.format(LocalDate.parse(isoDate))
    }.getOrElse { isoDate }
}
