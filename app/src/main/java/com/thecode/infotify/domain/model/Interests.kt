package com.thecode.infotify.domain.model

/**
 * What the user has chosen to follow.
 *
 * The shape is dictated by the provider: a query accepts at most five categories and at
 * most five countries. Rather than let the user pick freely and silently truncate, the
 * app states the limit — up to five subjects, and one region. A constraint shown honestly
 * produces a focused feed; an unlimited list would have produced a "For you" tab as
 * generic as the home feed.
 */
data class Interests(
    val topics: Set<Topic> = emptySet(),
    val region: Region? = null
) {
    val isEmpty: Boolean get() = topics.isEmpty() && region == null

    val canAddTopic: Boolean get() = topics.size < Topic.MAX_SELECTED

    /**
     * Adds or removes [topic]. Adding beyond the cap is ignored rather than throwing:
     * the UI disables the control, and a race should not crash the app.
     */
    fun toggle(topic: Topic): Interests = when {
        topic in topics -> copy(topics = topics - topic)
        canAddTopic -> copy(topics = topics + topic)
        else -> this
    }

    /** Selecting the current region again clears it, so the choice is never a one-way door. */
    fun toggle(region: Region): Interests =
        copy(region = if (this.region == region) null else region)

    companion object {
        val None = Interests()
    }
}
