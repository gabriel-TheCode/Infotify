package com.thecode.infotify.utils

object AppConstants {
    const val PREFERENCE_NAME = "com.thecode.infotify"

    /**
     * Infotify's own proxy. The app holds no API key: the key lives on the server, which
     * also caches responses so that many users cost one upstream credit.
     *
     * Deliberately a separate host from the marketing site at infotify.nativia.co. A
     * public API and a brochure should never share a document root: the site changes
     * often and the API must not, yet one rewrite file would govern both. Moved before
     * the first release, because this URL is compiled into every APK — after release it
     * can never be retired.
     */
    const val INFOTIFY_BASE_URL = "https://infotify-api.nativia.co/"

    const val DEFAULT_LANGUAGE = "en"

    /**
     * 20s. Beyond this the user has long concluded the app is broken; failing fast lets
     * the error state appear while they still care.
     */
    const val REQUEST_TIMEOUT_SECONDS = 20L
}
