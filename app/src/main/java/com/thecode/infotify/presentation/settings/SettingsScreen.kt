package com.thecode.infotify.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Interests
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.R
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.domain.model.Interests as UserInterests
import com.thecode.infotify.domain.model.Language
import com.thecode.infotify.domain.model.ThemeMode
import java.time.LocalTime
import com.thecode.infotify.presentation.interests.labelRes as topicLabelRes

/**
 * Settings, organised the way a settings screen should be.
 *
 * Three rules hold throughout, and they are what the previous flat list of radio buttons
 * got wrong: every row shows its current value, so you know where you stand without
 * opening it; a choice of three or fewer never takes three rows; and any long choice —
 * language, interests — goes to its own page instead of unrolling here.
 */
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onOpenInterests: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenAbout: () -> Unit,
    onToggleDailyBriefing: (Boolean) -> Unit,
    onBriefingTimeSelected: (LocalTime) -> Unit,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        BriefingTimePickerDialog(
            initial = uiState.briefingTime,
            onConfirm = {
                onBriefingTimeSelected(it)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Section(stringResource(R.string.settings_section_content)) {
            NavigationRow(
                icon = Icons.Outlined.Interests,
                title = stringResource(R.string.interests_title),
                value = uiState.interests.summary(),
                onClick = onOpenInterests
            )
            RowDivider()
            NavigationRow(
                icon = Icons.Outlined.Language,
                title = stringResource(R.string.settings_language),
                value = uiState.language.label,
                onClick = onOpenLanguage
            )
        }

        Section(stringResource(R.string.settings_section_notifications)) {
            SwitchRow(
                icon = Icons.Outlined.NotificationsNone,
                title = stringResource(R.string.settings_daily_briefing),
                description = stringResource(R.string.settings_daily_briefing_description),
                checked = uiState.dailyBriefingEnabled,
                onCheckedChange = onToggleDailyBriefing
            )

            // The hour only exists when the briefing does. Showing a disabled time row
            // under an off switch is clutter that explains nothing.
            AnimatedVisibility(
                visible = uiState.dailyBriefingEnabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    RowDivider()
                    NavigationRow(
                        icon = Icons.Outlined.Schedule,
                        title = stringResource(R.string.settings_briefing_time),
                        value = uiState.briefingTimeLabel,
                        onClick = { showTimePicker = true }
                    )
                }
            }
        }

        Section(stringResource(R.string.settings_section_appearance)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DarkMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.settings_theme),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 14.dp)
                    )
                }
                // Three options fit on one row. The old screen spent three full rows and a
                // radio group on exactly this choice.
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    ThemeMode.entries.forEachIndexed { index, mode ->
                        SegmentedButton(
                            selected = mode == uiState.themeMode,
                            onClick = { onThemeModeSelected(mode) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ThemeMode.entries.size
                            ),
                            // The default selected state inserts a leading checkmark, which
                            // pushes the label sideways and wraps it onto a second line while
                            // the unselected segments stay on one. Selection is already
                            // obvious from the container colour, so the icon only does harm.
                            icon = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(mode.labelRes),
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Section(stringResource(R.string.settings_section_data)) {
            NavigationRow(
                icon = Icons.Outlined.DeleteSweep,
                title = stringResource(R.string.settings_clear_cache),
                value = uiState.cacheSize,
                onClick = onClearCache,
                showChevron = false
            )
        }

        Section(stringResource(R.string.settings_section_about)) {
            NavigationRow(
                icon = Icons.Outlined.Info,
                title = stringResource(R.string.about_title),
                value = stringResource(R.string.about_publisher_name),
                onClick = onOpenAbout
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, top = 24.dp, bottom = 8.dp)
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun RowDivider() = HorizontalDivider(
    color = MaterialTheme.colorScheme.outlineVariant,
    modifier = Modifier.padding(start = 50.dp)
)

/** A row that shows its current value: you know where you stand without opening it. */
@Composable
private fun NavigationRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    showChevron: Boolean = true
) {
    Surface(onClick = onClick, color = MaterialTheme.colorScheme.surface) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (showChevron) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp, end = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

/** "Technologie, Sciences +2" — the value, not the count. */
@Composable
private fun UserInterests.summary(): String {
    if (isEmpty) return stringResource(R.string.settings_interests_none)
    val names = topics.take(2).map { stringResource(it.topicLabelRes) }
    val remaining = topics.size - names.size + if (region != null) 1 else 0
    val head = names.joinToString(", ")
    return if (remaining > 0) "$head +$remaining" else head
}

val ThemeMode.labelRes: Int
    get() = when (this) {
        ThemeMode.System -> R.string.theme_system
        ThemeMode.Light -> R.string.theme_light
        ThemeMode.Dark -> R.string.theme_dark
    }

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() = InfotifyTheme {
    SettingsScreen(
        uiState = SettingsUiState(language = Language.French),
        onThemeModeSelected = {},
        onOpenInterests = {},
        onOpenLanguage = {},
        onOpenAbout = {},
        onToggleDailyBriefing = {},
        onBriefingTimeSelected = {},
        onClearCache = {}
    )
}
