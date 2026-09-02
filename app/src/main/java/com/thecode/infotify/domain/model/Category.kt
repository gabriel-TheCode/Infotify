package com.thecode.infotify.domain.model

/**
 * Categories the app exposes, mapped onto NewsData.io category values.
 * [Top] is the provider's editor-curated feed and is the app's default tab.
 */
enum class Category(val apiValue: String) {
    Top("top"),
    World("world"),
    Business("business"),
    Technology("technology"),
    Science("science"),
    Health("health"),
    Sports("sports"),
    Entertainment("entertainment"),
    Environment("environment"),
    Politics("politics");

    companion object {
        val Default: Category = Top

        fun fromApiValue(value: String): Category? =
            entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) }
    }
}
