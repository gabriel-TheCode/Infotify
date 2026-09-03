package com.thecode.infotify.domain.model

/**
 * A subject the user can follow. Values are the provider's category codes, verified
 * against the live API — anything not in this list is rejected upstream.
 *
 * Replaces the previous `Category` enum, which exposed 10 of the 17 available subjects.
 * Note what is deliberately absent: "Société" and "Afrique" are not subjects at the
 * provider. The first has no equivalent; the second is a region, modelled in [Region].
 */
enum class Topic(val apiValue: String) {
    Top("top"),
    World("world"),
    Politics("politics"),
    Business("business"),
    Technology("technology"),
    Science("science"),
    Health("health"),
    Sports("sports"),
    Entertainment("entertainment"),
    Environment("environment"),
    Education("education"),
    Crime("crime"),
    Food("food"),
    Tourism("tourism"),
    Lifestyle("lifestyle");

    companion object {
        val Default: Topic = Top

        /** The provider rejects a query carrying more than five categories. */
        const val MAX_SELECTED = 5

        fun fromApiValue(value: String): Topic? =
            entries.firstOrNull { it.apiValue.equals(value, ignoreCase = true) }
    }
}
