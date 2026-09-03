package com.thecode.infotify.presentation.reader

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.toColorInt
import com.thecode.infotify.R
import com.thecode.infotify.domain.model.Article

/**
 * Opens an article at its publisher, in a Custom Tab tinted to match the app.
 *
 * This replaces the previous WebView-inside-an-AlertDialog, which was not scrollable, not
 * clickable, and never destroyed. It is also the only compliant option: no news provider
 * licenses full-text redistribution, and the free NewsData.io plan does not return an
 * article body at all — so the reader belongs at the source.
 */
object ArticleReader {

    fun open(context: Context, url: String) {
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .setDefaultColorSchemeParams(
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(TOOLBAR_LIGHT.toColorInt())
                    .build()
            )
            .setColorSchemeParams(
                CustomTabsIntent.COLOR_SCHEME_DARK,
                CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(TOOLBAR_DARK.toColorInt())
                    .build()
            )
            .build()

        try {
            intent.launchUrl(context, Uri.parse(url))
        } catch (e: ActivityNotFoundException) {
            // No browser at all: fall back to whatever can view the link, and stay silent
            // if nothing can rather than crashing on the user's tap.
            runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }

    fun share(context: Context, article: Article) {
        val text = context.getString(R.string.share_article_text, article.title, article.url)
        val intent = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_SUBJECT, article.title)
            },
            context.getString(R.string.action_share)
        )
        runCatching { context.startActivity(intent) }
    }

    private const val TOOLBAR_LIGHT = "#FFFFFF"
    private const val TOOLBAR_DARK = "#1D1B1A"
}
