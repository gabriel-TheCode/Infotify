package com.thecode.infotify.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thecode.infotify.R
import com.thecode.infotify.designsystem.component.Wordmark
import com.thecode.infotify.presentation.about.AboutScreen
import com.thecode.infotify.presentation.bookmarks.BookmarksEffect
import com.thecode.infotify.presentation.bookmarks.BookmarksIntent
import com.thecode.infotify.presentation.bookmarks.BookmarksScreen
import com.thecode.infotify.presentation.bookmarks.BookmarksViewModel
import com.thecode.infotify.presentation.feed.FeedEffect
import com.thecode.infotify.presentation.feed.FeedScreen
import com.thecode.infotify.presentation.feed.FeedViewModel
import com.thecode.infotify.presentation.interests.InterestsScreen
import com.thecode.infotify.presentation.interests.InterestsViewModel
import com.thecode.infotify.presentation.language.LanguageScreen
import com.thecode.infotify.presentation.navigation.Route
import com.thecode.infotify.presentation.navigation.TopLevelDestination
import com.thecode.infotify.presentation.onboarding.OnboardingRoute
import com.thecode.infotify.presentation.reader.ArticleReader
import com.thecode.infotify.presentation.search.SearchEffect
import com.thecode.infotify.presentation.search.SearchScreen
import com.thecode.infotify.presentation.search.SearchViewModel
import com.thecode.infotify.presentation.settings.SettingsScreen
import com.thecode.infotify.presentation.settings.SettingsViewModel

/**
 * @param startWithOnboarding decided once, before the first composition, from the stored
 *   flag. Passing it in rather than observing it here avoids the flash of the feed that a
 *   late-arriving preference would cause.
 */
@Composable
fun InfotifyRoot(
    startWithOnboarding: Boolean,
    openForYou: Boolean = false
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (startWithOnboarding) Route.Onboarding else Route.Main
    ) {
        composable<Route.Onboarding> {
            OnboardingRoute(
                onFinished = {
                    navController.navigate(Route.Main) {
                        // Onboarding is never on the back stack: pressing back from the
                        // feed should leave the app, not replay the introduction.
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Main> {
            MainScaffold(openForYou = openForYou)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold(openForYou: Boolean) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val currentTop = TopLevelDestination.entries.firstOrNull { destination ->
        currentDestination?.hierarchyHasRoute(destination) == true
    }
    val isSubPage = currentTop == null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // The wordmark stands in for the title on the feed; every other screen
                    // names itself.
                    if (currentTop == TopLevelDestination.Feed) {
                        Wordmark(fontSize = 22, animated = true)
                    } else {
                        Text(
                            text = stringResource(currentDestination.titleRes(currentTop)),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                },
                navigationIcon = {
                    if (isSubPage) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            // The bar slides away on sub-pages instead of vanishing, so the change of
            // level is legible rather than abrupt.
            AnimatedVisibility(
                visible = !isSubPage,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    TopLevelDestination.entries.forEach { destination ->
                        val selected = destination == currentTop
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navController.navigateToTopLevel(destination) },
                            icon = {
                                Icon(
                                    imageVector = if (selected) {
                                        destination.selectedIcon
                                    } else {
                                        destination.unselectedIcon
                                    },
                                    contentDescription = null
                                )
                            },
                            label = { Text(stringResource(destination.labelRes)) }
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Feed,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable<Route.Feed> {
                FeedDestination(
                    snackbarHostState = snackbarHostState,
                    openForYou = openForYou,
                    onOpenInterests = { navController.navigate(Route.Interests) }
                )
            }
            composable<Route.Search> { SearchDestination(snackbarHostState) }
            composable<Route.Bookmarks> { BookmarksDestination(snackbarHostState) }

            composable<Route.Settings> {
                val viewModel: SettingsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                SettingsScreen(
                    uiState = uiState,
                    onThemeModeSelected = viewModel::onThemeModeSelected,
                    onOpenInterests = { navController.navigate(Route.Interests) },
                    onOpenLanguage = { navController.navigate(Route.Language) },
                    onOpenAbout = { navController.navigate(Route.About) },
                    onToggleDailyBriefing = viewModel::onDailyBriefingChanged,
                    onBriefingTimeSelected = viewModel::onBriefingTimeSelected,
                    onClearCache = viewModel::onClearCache
                )
            }

            composable<Route.Interests> {
                val viewModel: InterestsViewModel = hiltViewModel()
                val interests by viewModel.uiState.collectAsStateWithLifecycle()
                InterestsScreen(
                    interests = interests,
                    onToggleTopic = viewModel::onToggleTopic,
                    onToggleRegion = viewModel::onToggleRegion
                )
            }

            composable<Route.Language> {
                val viewModel: SettingsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                LanguageScreen(
                    selected = uiState.language,
                    onSelect = viewModel::onLanguageSelected
                )
            }

            composable<Route.About> { AboutScreen() }
        }
    }
}

@Composable
private fun FeedDestination(
    snackbarHostState: SnackbarHostState,
    openForYou: Boolean,
    onOpenInterests: () -> Unit
) {
    val viewModel: FeedViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val bookmarkAdded = stringResource(R.string.snackbar_bookmark_added)
    val bookmarkRemoved = stringResource(R.string.snackbar_bookmark_removed)
    val genericError = stringResource(R.string.snackbar_refresh_failed)

    // The daily briefing deep-links here; "For you" is the feed it was built from.
    LaunchedEffect(openForYou) {
        if (openForYou) {
            viewModel.onIntent(
                com.thecode.infotify.presentation.feed.FeedIntent.SelectMode(
                    com.thecode.infotify.presentation.feed.FeedMode.ForYou
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is FeedEffect.OpenReader -> ArticleReader.open(context, effect.url)
                is FeedEffect.Share -> ArticleReader.share(context, effect.article)
                is FeedEffect.ShowMessage -> snackbarHostState.showSnackbar(
                    message = if (effect.bookmarkAdded) bookmarkAdded else bookmarkRemoved,
                    duration = SnackbarDuration.Short
                )

                is FeedEffect.ShowError -> snackbarHostState.showSnackbar(
                    message = genericError,
                    duration = SnackbarDuration.Short
                )

                FeedEffect.NavigateToInterests -> onOpenInterests()
            }
        }
    }

    FeedScreen(uiState = uiState, onIntent = viewModel::onIntent)
}

@Composable
private fun SearchDestination(snackbarHostState: SnackbarHostState) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val bookmarkAdded = stringResource(R.string.snackbar_bookmark_added)
    val bookmarkRemoved = stringResource(R.string.snackbar_bookmark_removed)
    val genericError = stringResource(R.string.snackbar_refresh_failed)

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SearchEffect.OpenReader -> ArticleReader.open(context, effect.url)
                is SearchEffect.Share -> ArticleReader.share(context, effect.article)
                is SearchEffect.ShowMessage -> snackbarHostState.showSnackbar(
                    message = if (effect.bookmarkAdded) bookmarkAdded else bookmarkRemoved,
                    duration = SnackbarDuration.Short
                )

                is SearchEffect.ShowError -> snackbarHostState.showSnackbar(
                    message = genericError,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    SearchScreen(uiState = uiState, onIntent = viewModel::onIntent)
}

@Composable
private fun BookmarksDestination(snackbarHostState: SnackbarHostState) {
    val viewModel: BookmarksViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val removedMessage = stringResource(R.string.snackbar_bookmark_removed)
    val undoLabel = stringResource(R.string.action_undo)

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is BookmarksEffect.OpenReader -> ArticleReader.open(context, effect.url)
                is BookmarksEffect.Share -> ArticleReader.share(context, effect.article)

                // Removal is never final until the snackbar times out.
                is BookmarksEffect.Removed -> {
                    val result = snackbarHostState.showSnackbar(
                        message = removedMessage,
                        actionLabel = undoLabel,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(BookmarksIntent.UndoRemove(effect.article))
                    }
                }
            }
        }
    }

    BookmarksScreen(uiState = uiState, onIntent = viewModel::onIntent)
}

private fun NavDestination?.titleRes(top: TopLevelDestination?): Int = when {
    top != null -> top.labelRes
    this?.hasRoute(Route.Interests::class) == true -> R.string.interests_title
    this?.hasRoute(Route.Language::class) == true -> R.string.settings_language
    this?.hasRoute(Route.About::class) == true -> R.string.about_title
    else -> R.string.app_name
}

private fun NavDestination.hierarchyHasRoute(destination: TopLevelDestination): Boolean =
    hierarchy.any { it.hasRoute(destination.route::class) }

private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        // Tapping a tab returns to its root and keeps each tab's own scroll position.
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
