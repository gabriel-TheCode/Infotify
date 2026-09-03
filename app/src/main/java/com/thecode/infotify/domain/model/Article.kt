package com.thecode.infotify.domain.model

import java.time.Instant

/**
 * A single news article as the app understands it, independent of any provider.
 *
 * Note: the article body is deliberately absent. No news provider licenses full-text
 * redistribution, and NewsData.io's free plan does not return it at all. Reading happens
 * at the publisher, opened through Custom Tabs.
 */
data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAt: Instant,
    val source: ArticleSource,
    val categories: List<Topic>
)

data class ArticleSource(
    val id: String,
    val name: String,
    val iconUrl: String?
)

/**
 * One page of articles plus the cursor for the next one.
 * [nextCursor] is null when the provider has nothing more to give.
 */
data class ArticlePage(
    val articles: List<Article>,
    val nextCursor: String?
)
