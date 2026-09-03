package com.thecode.infotify.presentation.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.designsystem.component.ArticleCard
import com.thecode.infotify.designsystem.component.ArticleListSkeleton
import com.thecode.infotify.designsystem.component.TopicChipRow
import com.thecode.infotify.designsystem.component.EmptyFeedPanel
import com.thecode.infotify.designsystem.component.ErrorPanel
import com.thecode.infotify.designsystem.component.FeaturedArticleCard
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.domain.model.Article
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    uiState: FeedUiState,
    onIntent: (FeedIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Prefetch one screen ahead rather than at the very bottom, so the next page is
    // usually already there when the user reaches it.
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && lastVisible >= total - PREFETCH_DISTANCE
        }
    }

    LaunchedEffect(listState, uiState.canAppend) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .filter { it && uiState.canAppend }
            .collect { onIntent(FeedIntent.LoadMore) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        FeedModeTabs(
            mode = uiState.mode,
            onSelect = { onIntent(FeedIntent.SelectMode(it)) },
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )

        // Explore keeps every subject one tap away, so personalisation never closes the
        // door on the rest of the news.
        if (uiState.mode == FeedMode.Explore) {
            TopicChipRow(
                selected = uiState.topic,
                onSelect = { onIntent(FeedIntent.SelectTopic(it)) },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (uiState.showsInterestsPrompt) {
            PersonaliseBanner(
                onClick = { onIntent(FeedIntent.OpenInterests) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { onIntent(FeedIntent.Refresh) },
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState.phase) {
                FeedUiState.Phase.Loading -> ArticleListSkeleton()

                FeedUiState.Phase.Empty -> EmptyFeedPanel(
                    onRetry = { onIntent(FeedIntent.Retry) }
                )

                FeedUiState.Phase.Error -> ErrorPanel(
                    error = uiState.error ?: AppError.Unexpected(null),
                    onRetry = { onIntent(FeedIntent.Retry) }
                )

                FeedUiState.Phase.Content -> FeedList(
                    articles = uiState.articles,
                    bookmarkedUrls = uiState.bookmarkedUrls,
                    isAppending = uiState.isAppending,
                    listState = listState,
                    onIntent = onIntent
                )
            }
        }
    }
}

@Composable
private fun FeedList(
    articles: List<Article>,
    bookmarkedUrls: Set<String>,
    isAppending: Boolean,
    listState: LazyListState,
    onIntent: (FeedIntent) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // The first story carries the visual weight; the rest form a dense, scannable list.
        articles.firstOrNull()?.let { featured ->
            item(key = featured.url) {
                FeaturedArticleCard(
                    article = featured,
                    isBookmarked = featured.url in bookmarkedUrls,
                    onClick = { onIntent(FeedIntent.OpenArticle(featured)) },
                    onBookmark = { onIntent(FeedIntent.ToggleBookmark(featured)) },
                    onShare = { onIntent(FeedIntent.ShareArticle(featured)) }
                )
            }
        }

        items(
            items = articles.drop(1),
            key = { it.url }
        ) { article ->
            ArticleCard(
                article = article,
                isBookmarked = article.url in bookmarkedUrls,
                onClick = { onIntent(FeedIntent.OpenArticle(article)) },
                onBookmark = { onIntent(FeedIntent.ToggleBookmark(article)) },
                onShare = { onIntent(FeedIntent.ShareArticle(article)) }
            )
        }

        if (isAppending) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private const val PREFETCH_DISTANCE = 4

@Preview(showBackground = true)
@Composable
private fun FeedScreenPreview() = InfotifyTheme {
    FeedScreen(
        uiState = FeedUiState(phase = FeedUiState.Phase.Loading),
        onIntent = {}
    )
}

/**
 * "For you" against "Explore".
 *
 * Two tabs rather than a single algorithmic feed: the personalised view answers "what
 * matters to me", and the other half of the screen guarantees the rest of the news stays
 * one tap away.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedModeTabs(
    mode: FeedMode,
    onSelect: (FeedMode) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.PrimaryTabRow(
        selectedTabIndex = mode.ordinal,
        modifier = modifier,
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        divider = {}
    ) {
        FeedMode.entries.forEach { entry ->
            androidx.compose.material3.Tab(
                selected = entry == mode,
                onClick = { onSelect(entry) },
                text = {
                    androidx.compose.material3.Text(
                        text = androidx.compose.ui.res.stringResource(
                            when (entry) {
                                FeedMode.ForYou -> com.thecode.infotify.R.string.feed_for_you
                                FeedMode.Explore -> com.thecode.infotify.R.string.feed_explore
                            }
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
        }
    }
}

/**
 * Shown only when no interests are set. Non-blocking by design: the app works perfectly
 * without personalisation, it is simply better with it.
 */
@Composable
private fun PersonaliseBanner(onClick: () -> Unit, modifier: Modifier = Modifier) {
    androidx.compose.material3.Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                androidx.compose.material3.Text(
                    text = androidx.compose.ui.res.stringResource(
                        com.thecode.infotify.R.string.feed_personalise_title
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                androidx.compose.material3.Text(
                    text = androidx.compose.ui.res.stringResource(
                        com.thecode.infotify.R.string.feed_personalise_body
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
            }
            androidx.compose.material3.TextButton(onClick = onClick) {
                androidx.compose.material3.Text(
                    androidx.compose.ui.res.stringResource(
                        com.thecode.infotify.R.string.feed_personalise_action
                    )
                )
            }
        }
    }
}
