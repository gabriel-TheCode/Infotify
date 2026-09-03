package com.thecode.infotify.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.R
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.designsystem.theme.InfotifyTheme

/**
 * The single component for every non-content state.
 *
 * Empty and error are rendered by the same component but are never the same state: the
 * old build routed "no results" through the network-error path, so a perfectly good
 * response told the user to check their connection.
 */
@Composable
fun StatePanel(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(percent = 50),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(18.dp)
                    .size(28.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        if (actionLabel != null && onAction != null) {
            Button(
                onClick = onAction,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(actionLabel)
            }
        }
    }
}

/** Error panel wired to the domain's named errors, so the message always matches the cause. */
@Composable
fun ErrorPanel(
    error: AppError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, title, message) = when (error) {
        AppError.NoConnection -> Triple(
            Icons.Outlined.CloudOff,
            stringRes(R.string.error_offline_title),
            stringRes(R.string.error_offline_message)
        )

        AppError.QuotaExceeded -> Triple(
            Icons.Outlined.HourglassEmpty,
            stringRes(R.string.error_quota_title),
            stringRes(R.string.error_quota_message)
        )

        AppError.InvalidCredentials -> Triple(
            Icons.Outlined.ErrorOutline,
            stringRes(R.string.error_credentials_title),
            stringRes(R.string.error_credentials_message)
        )

        is AppError.Server, is AppError.Unexpected -> Triple(
            Icons.Outlined.ErrorOutline,
            stringRes(R.string.error_generic_title),
            stringRes(R.string.error_generic_message)
        )
    }

    StatePanel(
        icon = icon,
        title = title,
        message = message,
        modifier = modifier,
        // Retrying a quota failure cannot succeed, so no button is offered for it.
        actionLabel = if (error == AppError.QuotaExceeded) null else stringRes(R.string.action_retry),
        onAction = if (error == AppError.QuotaExceeded) null else onRetry
    )
}

@Composable
fun EmptyFeedPanel(onRetry: () -> Unit, modifier: Modifier = Modifier) = StatePanel(
    icon = Icons.Outlined.SearchOff,
    title = stringRes(R.string.empty_feed_title),
    message = stringRes(R.string.empty_feed_message),
    modifier = modifier,
    actionLabel = stringRes(R.string.action_refresh),
    onAction = onRetry
)

@Composable
fun EmptyBookmarksPanel(modifier: Modifier = Modifier) = StatePanel(
    icon = Icons.Outlined.BookmarkBorder,
    title = stringRes(R.string.empty_bookmarks_title),
    message = stringRes(R.string.empty_bookmarks_message),
    modifier = modifier
)

@Composable
private fun stringRes(id: Int): String =
    androidx.compose.ui.res.stringResource(id)

/** Placeholder rows shown while the first page loads, instead of a blocking spinner. */
@Composable
fun ArticleListSkeleton(modifier: Modifier = Modifier, count: Int = 6) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(count) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
            ) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPanelPreview() = InfotifyTheme {
    ErrorPanel(error = AppError.NoConnection, onRetry = {})
}
