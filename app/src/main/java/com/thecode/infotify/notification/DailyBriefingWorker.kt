package com.thecode.infotify.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thecode.infotify.R
import com.thecode.infotify.core.result.Outcome
import com.thecode.infotify.domain.model.Article
import com.thecode.infotify.domain.repository.PreferencesRepository
import com.thecode.infotify.domain.usecase.GetForYouNews
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Delivers one briefing a day.
 *
 * The design rule that matters most is the silence rule: if nothing has been published in
 * the user's subjects since the last briefing, **no notification is sent**. A daily
 * "there is news today" that fires regardless of whether there is news teaches people to
 * ignore the app, then to uninstall it.
 *
 * The notification also carries the news rather than announcing it: the best headline plus
 * a count. Something is read even when the notification is not opened, which is what makes
 * a daily interruption tolerable over months.
 */
@HiltWorker
class DailyBriefingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val getForYouNews: GetForYouNews,
    private val preferences: PreferencesRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!preferences.dailyBriefingEnabled().first()) return Result.success()
        if (!hasNotificationPermission()) return Result.success()

        val interests = preferences.interests().first()
        val language = preferences.languageCode().first()

        val outcome = getForYouNews(interests, language)
        // A failed fetch is not worth waking anyone for. Retry once; the next day's run
        // will happen regardless.
        val page = (outcome as? Outcome.Success)?.data ?: return Result.retry()

        val since = Instant.now().minus(BRIEFING_WINDOW_HOURS, ChronoUnit.HOURS)
        val fresh = page.articles.filter { it.publishedAt.isAfter(since) }

        if (fresh.isEmpty()) return Result.success()

        @Suppress("MissingPermission")
        notify(fresh)
        return Result.success()
    }

    // hasNotificationPermission() gates every call site; lint cannot see across that check.
    @androidx.annotation.RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun notify(articles: List<Article>) {
        val lead = articles.first()
        val others = articles.size - 1

        val title = if (others > 0) {
            context.resources.getQuantityString(
                R.plurals.briefing_title,
                others,
                others
            )
        } else {
            context.getString(R.string.briefing_title_single)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            // The headline itself, not a teaser about it.
            .setContentText(lead.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(lead.title))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openForYouIntent())
            .build()

        createChannel()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    /** Deep link straight to the personalised feed the briefing came from. */
    private fun openForYouIntent(): PendingIntent {
        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_OPEN_FOR_YOU, true)
            }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.briefing_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.briefing_channel_description)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    private fun hasNotificationPermission(): Boolean =
        android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    companion object {
        const val WORK_NAME = "infotify_daily_briefing"
        const val EXTRA_OPEN_FOR_YOU = "open_for_you"

        private const val CHANNEL_ID = "daily_briefing"
        private const val NOTIFICATION_ID = 1001

        /** Only articles published since roughly the last briefing count as "new". */
        private const val BRIEFING_WINDOW_HOURS = 24L
    }
}
