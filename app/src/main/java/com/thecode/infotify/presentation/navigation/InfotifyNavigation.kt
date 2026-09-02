package com.thecode.infotify.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.ui.graphics.vector.ImageVector
import com.thecode.infotify.R
import kotlinx.serialization.Serializable

/**
 * Typed routes. Replaces the previous mix of a ViewPager2 with hardcoded positions and
 * explicit Intents between Activities.
 */
@Serializable
sealed interface Route {
    @Serializable
    data object Feed : Route

    @Serializable
    data object Search : Route

    @Serializable
    data object Bookmarks : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Onboarding : Route
}

/**
 * Top-level destinations. Opening an article is not one of them: reading happens in a
 * Custom Tab, which is an effect, not a destination.
 */
enum class TopLevelDestination(
    val route: Route,
    @StringRes val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Feed(
        route = Route.Feed,
        labelRes = R.string.nav_feed,
        selectedIcon = Icons.Filled.Whatshot,
        unselectedIcon = Icons.Outlined.Whatshot
    ),
    Search(
        route = Route.Search,
        labelRes = R.string.nav_search,
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    ),
    Bookmarks(
        route = Route.Bookmarks,
        labelRes = R.string.nav_bookmarks,
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder
    ),
    Settings(
        route = Route.Settings,
        labelRes = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}
