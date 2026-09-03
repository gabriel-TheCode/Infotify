package com.thecode.infotify.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.R
import com.thecode.infotify.core.result.AppError
import com.thecode.infotify.designsystem.component.ArticleCard
import com.thecode.infotify.designsystem.component.ArticleListSkeleton
import com.thecode.infotify.designsystem.component.ErrorPanel
import com.thecode.infotify.designsystem.component.StatePanel
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current

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
            .collect { onIntent(SearchIntent.LoadMore) }
    }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = { onIntent(SearchIntent.QueryChanged(it)) },
            placeholder = { Text(stringResource(R.string.search_placeholder)) },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null)
            },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { onIntent(SearchIntent.Clear) }) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.action_clear_search)
                        )
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboard?.hide()
                    onIntent(SearchIntent.Submit)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        when (uiState.phase) {
            SearchUiState.Phase.Idle -> StatePanel(
                icon = Icons.Outlined.Search,
                title = stringResource(R.string.search_idle_title),
                message = stringResource(R.string.search_idle_message)
            )

            SearchUiState.Phase.Loading -> ArticleListSkeleton()

            SearchUiState.Phase.Empty -> StatePanel(
                icon = Icons.Outlined.Search,
                title = stringResource(R.string.search_empty_title),
                message = stringResource(R.string.search_empty_message, uiState.query)
            )

            SearchUiState.Phase.Error -> ErrorPanel(
                error = uiState.error ?: AppError.Unexpected(null),
                onRetry = { onIntent(SearchIntent.Retry) }
            )

            SearchUiState.Phase.Content -> SearchResults(
                uiState = uiState,
                listState = listState,
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun SearchResults(
    uiState: SearchUiState,
    listState: LazyListState,
    onIntent: (SearchIntent) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = uiState.articles, key = { it.url }) { article ->
            ArticleCard(
                article = article,
                isBookmarked = article.url in uiState.bookmarkedUrls,
                onClick = { onIntent(SearchIntent.OpenArticle(article)) },
                onBookmark = { onIntent(SearchIntent.ToggleBookmark(article)) },
                onShare = { onIntent(SearchIntent.ShareArticle(article)) }
            )
        }

        if (uiState.isAppending) {
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
private fun SearchScreenPreview() = InfotifyTheme {
    SearchScreen(uiState = SearchUiState(), onIntent = {})
}
