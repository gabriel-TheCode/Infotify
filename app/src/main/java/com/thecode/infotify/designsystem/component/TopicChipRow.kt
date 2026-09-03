package com.thecode.infotify.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.domain.model.Topic
import com.thecode.infotify.presentation.interests.labelRes

/**
 * The Explore filter: every subject the provider offers, one tap away.
 *
 * This row is what keeps personalisation from becoming a bubble — discovery is half the
 * home screen rather than an option buried in settings.
 */
@Composable
fun TopicChipRow(
    selected: Topic,
    onSelect: (Topic) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    // Keep the active subject visible when it changes from elsewhere, so the row never
    // shows a selection the user cannot see.
    LaunchedEffect(selected) {
        val index = Topic.entries.indexOf(selected)
        if (index >= 0) state.animateScrollToItem(index)
    }

    LazyRow(
        state = state,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(Topic.entries, key = { it.name }) { topic ->
            FilterChip(
                selected = topic == selected,
                onClick = { onSelect(topic) },
                label = { Text(stringResource(topic.labelRes)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicChipRowPreview() = InfotifyTheme {
    TopicChipRow(selected = Topic.Top, onSelect = {})
}
