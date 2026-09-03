package com.thecode.infotify.domain.repository

import java.time.LocalTime

/**
 * Schedules the daily briefing.
 *
 * Declared in the domain so use cases can turn the feature on and off without knowing that
 * WorkManager exists.
 */
interface BriefingScheduler {

    fun schedule(at: LocalTime)

    fun cancel()
}
