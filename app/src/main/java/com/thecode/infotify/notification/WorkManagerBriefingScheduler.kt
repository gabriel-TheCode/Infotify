package com.thecode.infotify.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.thecode.infotify.domain.repository.BriefingScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerBriefingScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : BriefingScheduler {

    override fun schedule(at: LocalTime) {
        val request = PeriodicWorkRequestBuilder<DailyBriefingWorker>(Duration.ofDays(1))
            .setInitialDelay(delayUntilNext(at))
            .setConstraints(
                Constraints.Builder()
                    // No point waking to fetch news with no way to fetch it.
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DailyBriefingWorker.WORK_NAME,
            // REPLACE, so changing the time reschedules instead of adding a second job.
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    override fun cancel() {
        WorkManager.getInstance(context).cancelUniqueWork(DailyBriefingWorker.WORK_NAME)
    }

    /** Time until the next occurrence of [at]; if it has passed today, tomorrow's. */
    private fun delayUntilNext(at: LocalTime): Duration {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(at)
        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }
        return Duration.between(now, next)
    }
}
