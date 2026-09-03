package com.thecode.infotify.domain

import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Region
import com.thecode.infotify.domain.model.Topic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InterestsTest {

    @Test
    fun `toggling adds then removes a topic`() {
        val once = Interests.None.toggle(Topic.Science)
        assertTrue(Topic.Science in once.topics)
        assertFalse(Topic.Science in once.toggle(Topic.Science).topics)
    }

    /** The provider rejects more than five categories; the model enforces it. */
    @Test
    fun `a sixth topic is refused, not silently truncated at request time`() {
        val full = Topic.entries.take(5).fold(Interests.None) { acc, t -> acc.toggle(t) }
        assertEquals(5, full.topics.size)
        assertFalse(full.canAddTopic)

        val overflowed = full.toggle(Topic.entries[5])
        assertEquals(5, overflowed.topics.size)
        assertFalse(Topic.entries[5] in overflowed.topics)
    }

    @Test
    fun `a full selection can still be changed`() {
        val full = Topic.entries.take(5).fold(Interests.None) { acc, t -> acc.toggle(t) }
        val freed = full.toggle(Topic.entries[0])

        assertTrue(freed.canAddTopic)
        assertTrue(Topic.entries[5] in freed.toggle(Topic.entries[5]).topics)
    }

    @Test
    fun `selecting the same region twice clears it, so the choice is never one-way`() {
        val chosen = Interests.None.toggle(Region.Africa)
        assertEquals(Region.Africa, chosen.region)
        assertNull(chosen.toggle(Region.Africa).region)
    }

    @Test
    fun `regions carry exactly five country codes, the provider's cap`() {
        Region.entries.forEach { region ->
            assertEquals(region.name, 5, region.countryCodes.size)
        }
    }

    @Test
    fun `empty means empty on both axes`() {
        assertTrue(Interests.None.isEmpty)
        assertFalse(Interests.None.toggle(Region.Europe).isEmpty)
        assertFalse(Interests.None.toggle(Topic.Sports).isEmpty)
    }
}
