package com.thecode.infotify.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.thecode.infotify.R
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * "12 min", "3 h", "Yesterday", "12 Mar" — the phrasing a reader scans, not a raw date.
 *
 * The previous build printed publishedAt.split("T")[0], which gave every article from
 * today the same unhelpful "2026-09-02".
 */
@Composable
fun relativeTime(instant: Instant, now: Instant = Instant.now()): String {
    val context = LocalContext.current
    val minutes = ChronoUnit.MINUTES.between(instant, now)

    return when {
        minutes < 1 -> context.getString(R.string.time_just_now)
        minutes < 60 -> context.getString(R.string.time_minutes, minutes)
        minutes < 60 * 24 -> context.getString(R.string.time_hours, minutes / 60)
        minutes < 60 * 48 -> context.getString(R.string.time_yesterday)
        else -> {
            val days = minutes / (60 * 24)
            if (days < 7) {
                context.getString(R.string.time_days, days)
            } else {
                context.getString(R.string.time_weeks, days / 7)
            }
        }
    }
}
