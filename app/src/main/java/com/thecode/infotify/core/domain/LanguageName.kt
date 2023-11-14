package com.thecode.infotify.core.domain

enum class LanguageName(val label: String, val code: String, val apiCode: String) {

    ENGLISH("English", "en", "us"),
    FRENCH("Français", "fr", "fr"),
    DEUTSCH("Deutsch", "de", "de"),
    ITALIAN("Italiano", "it", "it"),
    NEDERLANDS("Nederlands", "nl", "nl"),
    RUSSIAN("русский", "ru", "ru"),
    ARABIC("العربية", "ar", "ar"),
    PORTUGUESE("Português", "pt", "pt");

    companion object {
        fun getLabels() = values().map { it.label }

        fun getCodes() = values().map { it.code }

        fun getApiCodes() = values().map { it.apiCode }
    }
}