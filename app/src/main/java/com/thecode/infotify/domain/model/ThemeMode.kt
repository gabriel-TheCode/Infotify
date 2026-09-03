package com.thecode.infotify.domain.model

/**
 * Replaces the previous boolean night-mode flag.
 *
 * A boolean cannot express "follow the system", which is the behaviour users expect by
 * default and which the old DayNightSwitch made impossible.
 */
enum class ThemeMode {
    System,
    Light,
    Dark;

    companion object {
        val Default: ThemeMode = System

        fun fromName(value: String?): ThemeMode =
            entries.firstOrNull { it.name == value } ?: Default
    }
}
