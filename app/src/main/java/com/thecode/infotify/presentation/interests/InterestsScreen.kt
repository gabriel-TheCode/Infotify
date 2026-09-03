package com.thecode.infotify.presentation.interests

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecode.infotify.R
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Region
import com.thecode.infotify.domain.model.Topic

@Composable
fun InterestsScreen(
    interests: Interests,
    onToggleTopic: (Topic) -> Unit,
    onToggleRegion: (Region) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.onboarding_interests_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )
        InterestPicker(
            interests = interests,
            onToggleTopic = onToggleTopic,
            onToggleRegion = onToggleRegion,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InterestsScreenPreview() = InfotifyTheme {
    InterestsScreen(
        interests = Interests(topics = setOf(Topic.Technology)),
        onToggleTopic = {},
        onToggleRegion = {}
    )
}
