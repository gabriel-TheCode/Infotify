package com.thecode.infotify.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.thecode.infotify.R
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * "12 min", "3 h", "yesterday", "2 w" — the phrasing a reader scans, not a raw date.
 *
 * The previous build printed publishedAt.split("T")[0], which gave every article published
 * today the same unhelpful "2026-09-02".
 *
 * Strings come from [stringResource] rather than LocalContext.current.getString: the latter
 * is not configuration-aware, so a locale change would leave stale text on screen until the
 * composable happened to be recreated for another reason.
 */
@Composable
fun relativeTime(instant: Instant, now: Instant = Instant.now()): String {
    val minutes = ChronoUnit.MINUTES.between(instant, now)
    val days = minutes / (60 * 24)

    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes, minutes)
        minutes < 60 * 24 -> stringResource(R.string.time_hours, minutes / 60)
        minutes < 60 * 48 -> stringResource(R.string.time_yesterday)
        days < 7 -> stringResource(R.string.time_days, days)
        else -> stringResource(R.string.time_weeks, days / 7)
    }
}
