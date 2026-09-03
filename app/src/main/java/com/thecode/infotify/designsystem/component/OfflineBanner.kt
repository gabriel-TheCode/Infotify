package com.thecode.infotify.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.R
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import java.time.Instant

/**
 * Says, plainly, that the articles below were saved earlier.
 *
 * Serving a remembered feed silently would be the worse failure: the reader would take
 * yesterday's headlines for today's. The banner names the age of what it is showing and
 * then gets out of the way — it is informative, not an error, so it uses a neutral
 * surface rather than the error colour.
 */
@Composable
fun OfflineBanner(cachedAt: Instant, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.offline_showing_saved, relativeTime(cachedAt)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OfflineBannerPreview() = InfotifyTheme {
    OfflineBanner(
        cachedAt = Instant.now().minusSeconds(5400),
        modifier = Modifier.padding(16.dp)
    )
}
