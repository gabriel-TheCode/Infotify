package com.thecode.infotify.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.R
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.domain.model.Category

/**
 * Scrollable category filter.
 *
 * Replaces ThemedToggleButtonGroup and its five hardcoded buttons — the provider offers
 * ten categories, and a LazyRow accommodates all of them without a layout rewrite.
 */
@Composable
fun CategoryChipRow(
    selected: Category,
    onSelect: (Category) -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState()
) {
    LazyRow(
        state = state,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(Category.entries, key = { it.name }) { category ->
            FilterChip(
                selected = category == selected,
                onClick = { onSelect(category) },
                label = { Text(stringResource(category.labelRes)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

/** Category labels live in resources so they stay translatable. */
val Category.labelRes: Int
    get() = when (this) {
        Category.Top -> R.string.category_top
        Category.World -> R.string.category_world
        Category.Business -> R.string.category_business
        Category.Technology -> R.string.category_technology
        Category.Science -> R.string.category_science
        Category.Health -> R.string.category_health
        Category.Sports -> R.string.category_sports
        Category.Entertainment -> R.string.category_entertainment
        Category.Environment -> R.string.category_environment
        Category.Politics -> R.string.category_politics
    }

@Preview(showBackground = true)
@Composable
private fun CategoryChipRowPreview() = InfotifyTheme {
    CategoryChipRow(selected = Category.Top, onSelect = {})
}
