package com.thecode.infotify.domain.model

/**
 * A part of the world the user can follow.
 *
 * Regions are not categories at the provider — they are sets of country codes. This is
 * what makes "Afrique" possible at all, and it is a better product than a subject list:
 * a reader in Douala can follow African coverage, not only abstract topics.
 *
 * Each region carries exactly five codes because the provider rejects a query with more
 * than five countries. The five are chosen for editorial coverage rather than population.
 */
enum class Region(val countryCodes: List<String>) {
    Africa(listOf("ng", "za", "ke", "cm", "ci")),
    Europe(listOf("fr", "de", "gb", "es", "it")),
    Americas(listOf("us", "ca", "br", "mx", "ar")),
    AsiaPacific(listOf("in", "cn", "jp", "au", "ae"));

    val query: String get() = countryCodes.joinToString(",")
}
