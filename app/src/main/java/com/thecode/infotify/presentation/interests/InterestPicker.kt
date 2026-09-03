package com.thecode.infotify.presentation.interests

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.R
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Region
import com.thecode.infotify.domain.model.Topic

/**
 * The one component that lets a user say what they care about. Shared by onboarding and
 * settings, so the two can never drift apart.
 *
 * The five-subject cap is the provider's, not a design preference: a query carrying more
 * than five categories is rejected. Stating it plainly — with a counter — turns a hard
 * limit into a prompt for intent, and keeps the personalised feed focused.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestPicker(
    interests: Interests,
    onToggleTopic: (Topic) -> Unit,
    onToggleRegion: (Region) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(R.string.interests_topics_title),
            counter = stringResource(
                R.string.interests_counter,
                interests.topics.size,
                Topic.MAX_SELECTED
            )
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Topic.entries.forEach { topic ->
                val selected = topic in interests.topics
                SelectableChip(
                    label = stringResource(topic.labelRes),
                    selected = selected,
                    // Disabling rather than silently ignoring: the cap has to be visible
                    // at the moment it bites, or it reads as a bug.
                    enabled = selected || interests.canAddTopic,
                    onClick = { onToggleTopic(topic) }
                )
            }
        }

        SectionHeader(
            title = stringResource(R.string.interests_region_title),
            subtitle = stringResource(R.string.interests_region_subtitle),
            modifier = Modifier.padding(top = 28.dp)
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Region.entries.forEach { region ->
                SelectableChip(
                    label = stringResource(region.labelRes),
                    selected = interests.region == region,
                    enabled = true,
                    onClick = { onToggleRegion(region) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    counter: String? = null
) {
    Column(modifier = modifier) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            if (counter != null) {
                Text(
                    text = counter,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

/**
 * A chip that animates its own selection rather than relying on a list-wide recomposition,
 * so tapping feels immediate even while the feed reloads behind it.
 */
@Composable
private fun SelectableChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val container by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.primary
            enabled -> MaterialTheme.colorScheme.surface
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = spring(stiffness = 900f),
        label = "chipContainer"
    )
    val content by animateColorAsState(
        targetValue = when {
            selected -> MaterialTheme.colorScheme.onPrimary
            enabled -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        },
        animationSpec = spring(stiffness = 900f),
        label = "chipContent"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.98f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 700f),
        label = "chipScale"
    )

    val selectedLabel = stringResource(R.string.interests_state_selected)
    val unselectedLabel = stringResource(R.string.interests_state_unselected)

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.extraLarge,
        color = container,
        contentColor = content,
        border = if (selected) null else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        },
        modifier = Modifier
            .scale(scale)
            .semantics {
                role = Role.Checkbox
                stateDescription = if (selected) selectedLabel else unselectedLabel
                contentDescription = label
            }
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 0.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = if (selected) 6.dp else 0.dp)
            )
        }
    }
}

val Topic.labelRes: Int
    get() = when (this) {
        Topic.Top -> R.string.topic_top
        Topic.World -> R.string.topic_world
        Topic.Politics -> R.string.topic_politics
        Topic.Business -> R.string.topic_business
        Topic.Technology -> R.string.topic_technology
        Topic.Science -> R.string.topic_science
        Topic.Health -> R.string.topic_health
        Topic.Sports -> R.string.topic_sports
        Topic.Entertainment -> R.string.topic_entertainment
        Topic.Environment -> R.string.topic_environment
        Topic.Education -> R.string.topic_education
        Topic.Crime -> R.string.topic_crime
        Topic.Food -> R.string.topic_food
        Topic.Tourism -> R.string.topic_tourism
        Topic.Lifestyle -> R.string.topic_lifestyle
    }

val Region.labelRes: Int
    get() = when (this) {
        Region.Africa -> R.string.region_africa
        Region.Europe -> R.string.region_europe
        Region.Americas -> R.string.region_americas
        Region.AsiaPacific -> R.string.region_asia_pacific
    }

@Preview(showBackground = true)
@Composable
private fun InterestPickerPreview() = InfotifyTheme {
    InterestPicker(
        interests = Interests(topics = setOf(Topic.Technology, Topic.Science)),
        onToggleTopic = {},
        onToggleRegion = {},
        modifier = Modifier.padding(20.dp)
    )
}
