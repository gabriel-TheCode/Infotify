package com.thecode.infotify.domain.usecase

import com.thecode.infotify.domain.repository.BriefingScheduler
import com.thecode.infotify.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import javax.inject.Inject

class ObserveDailyBriefing @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.dailyBriefingEnabled()
}

class ObserveBriefingTime @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<LocalTime> = repository.briefingTime()
}

/**
 * Turning the briefing on both records the preference and schedules the work, so the two
 * can never disagree — a switch that persists without scheduling would be a lie.
 */
class SetDailyBriefing @Inject constructor(
    private val repository: PreferencesRepository,
    private val scheduler: BriefingScheduler
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setDailyBriefingEnabled(enabled)
        if (enabled) {
            scheduler.schedule(repository.briefingTime().first())
        } else {
            scheduler.cancel()
        }
    }
}

class SetBriefingTime @Inject constructor(
    private val repository: PreferencesRepository,
    private val scheduler: BriefingScheduler
) {
    suspend operator fun invoke(time: LocalTime) {
        repository.setBriefingTime(time)
        scheduler.schedule(time)
    }
}
