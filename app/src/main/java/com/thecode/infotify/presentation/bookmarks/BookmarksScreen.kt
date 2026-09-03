package com.thecode.infotify.presentation.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.R
import com.thecode.infotify.designsystem.component.ArticleCard
import com.thecode.infotify.designsystem.component.ArticleListSkeleton
import com.thecode.infotify.designsystem.component.EmptyBookmarksPanel
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.domain.model.Article

@Composable
fun BookmarksScreen(
    uiState: BookmarksUiState,
    onIntent: (BookmarksIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState.phase) {
        BookmarksUiState.Phase.Loading -> ArticleListSkeleton(modifier = modifier)
        BookmarksUiState.Phase.Empty -> EmptyBookmarksPanel(modifier = modifier)
        BookmarksUiState.Phase.Content -> BookmarkList(
            articles = uiState.articles,
            onIntent = onIntent,
            modifier = modifier
        )
    }
}

@Composable
private fun BookmarkList(
    articles: List<Article>,
    onIntent: (BookmarksIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = articles, key = { it.url }) { article ->
            SwipeToRemove(
                onRemove = { onIntent(BookmarksIntent.Remove(article)) }
            ) {
                ArticleCard(
                    article = article,
                    isBookmarked = true,
                    onClick = { onIntent(BookmarksIntent.OpenArticle(article)) },
                    onBookmark = { onIntent(BookmarksIntent.Remove(article)) },
                    onShare = { onIntent(BookmarksIntent.ShareArticle(article)) }
                )
            }
        }
    }
}

/**
 * Swipe either way to remove. Removal is always paired with an Undo snackbar upstream,
 * so an accidental swipe costs nothing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToRemove(
    onRemove: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onRemove()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = state,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = stringResource(R.string.action_remove_bookmark),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = { content() }
    )
}

@Preview(showBackground = true)
@Composable
private fun BookmarksEmptyPreview() = InfotifyTheme {
    BookmarksScreen(
        uiState = BookmarksUiState(phase = BookmarksUiState.Phase.Empty),
        onIntent = {}
    )
}
