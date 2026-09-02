package com.thecode.infotify.domain.model

/**
 * Languages the app offers, with the ISO 639-1 code NewsData.io expects.
 *
 * Single source of truth: the previous build had an 8-entry enum and a 7-entry
 * string-array that disagreed with each other.
 */
enum class Language(val label: String, val code: String) {
    English("English", "en"),
    French("Français", "fr"),
    Spanish("Español", "es"),
    German("Deutsch", "de"),
    Italian("Italiano", "it"),
    Portuguese("Português", "pt"),
    Dutch("Nederlands", "nl"),
    Russian("Русский", "ru"),
    Arabic("العربية", "ar");

    companion object {
        val Default: Language = English

        fun fromCode(code: String?): Language =
            entries.firstOrNull { it.code == code } ?: Default
    }
}
