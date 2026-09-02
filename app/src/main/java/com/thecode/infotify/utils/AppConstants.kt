package com.thecode.infotify.utils

object AppConstants {
    const val PREFERENCE_NAME = "com.thecode.infotify"
    const val NEWSDATA_BASE_URL = "https://newsdata.io/"
    const val DEFAULT_LANGUAGE = "en"

    /**
     * 20s, down from 60s. Beyond this the user has long concluded the app is broken;
     * failing fast lets the error state appear while they still care.
     */
    const val REQUEST_TIMEOUT_SECONDS = 20L
}
