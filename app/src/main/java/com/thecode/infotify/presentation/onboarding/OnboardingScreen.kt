package com.thecode.infotify.presentation.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thecode.infotify.R
import com.thecode.infotify.designsystem.component.Wordmark
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.presentation.interests.InterestPicker

@Composable
fun OnboardingRoute(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnboardingScreen(
        uiState = uiState,
        onToggleTopic = viewModel::onToggleTopic,
        onToggleRegion = viewModel::onToggleRegion,
        onNext = viewModel::onNext,
        onFinish = { viewModel.onFinish(onFinished) }
    )
}

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onToggleTopic: (com.thecode.infotify.domain.model.Topic) -> Unit,
    onToggleRegion: (com.thecode.infotify.domain.model.Region) -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        StepIndicator(
            current = uiState.step.ordinal,
            total = OnboardingUiState.Step.entries.size,
            modifier = Modifier.padding(top = 24.dp)
        )

        AnimatedContent(
            targetState = uiState.step,
            transitionSpec = {
                // Forward moves left, backward moves right: the motion tells you which way
                // you are going through the flow.
                val forward = targetState.ordinal > initialState.ordinal
                val offset = if (forward) 1 else -1
                (
                    slideInHorizontally(tween(320)) { it / 6 * offset } +
                        fadeIn(tween(220))
                    ) togetherWith (
                    slideOutHorizontally(tween(320)) { -it / 6 * offset } +
                        fadeOut(tween(160))
                    )
            },
            label = "onboardingStep",
            modifier = Modifier.weight(1f)
        ) { step ->
            when (step) {
                OnboardingUiState.Step.Welcome -> WelcomeStep()
                OnboardingUiState.Step.Interests -> InterestsStep(
                    uiState = uiState,
                    onToggleTopic = onToggleTopic,
                    onToggleRegion = onToggleRegion
                )

                OnboardingUiState.Step.Notifications -> NotificationsStep()
            }
        }

        OnboardingActions(
            uiState = uiState,
            onNext = onNext,
            onFinish = onFinish
        )
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Wordmark(fontSize = 44)
        Text(
            text = stringResource(R.string.onboarding_welcome_title),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 28.dp)
        )
        Text(
            text = stringResource(R.string.onboarding_welcome_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun InterestsStep(
    uiState: OnboardingUiState,
    onToggleTopic: (com.thecode.infotify.domain.model.Topic) -> Unit,
    onToggleRegion: (com.thecode.infotify.domain.model.Region) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.onboarding_interests_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = stringResource(R.string.onboarding_interests_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
        InterestPicker(
            interests = uiState.interests,
            onToggleTopic = onToggleTopic,
            onToggleRegion = onToggleRegion
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun NotificationsStep() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.onboarding_notifications_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.onboarding_notifications_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
        NotificationPreview()
    }
}

/**
 * Shows the notification the user is being asked to accept, before asking.
 *
 * Permission dialogs get refused when people cannot picture what they are agreeing to;
 * a realistic preview is worth more than a paragraph of persuasion.
 */
@Composable
private fun NotificationPreview() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsNone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(7.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.onboarding_notifications_preview_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = stringResource(R.string.onboarding_notifications_preview_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OnboardingActions(
    uiState: OnboardingUiState,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    // Android 13+ requires an explicit runtime permission. It is requested here, at the end
    // of onboarding after the value has been shown — never on first launch.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { onFinish() }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 28.dp, top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                when {
                    !uiState.isLastStep -> onNext()
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                    else -> onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = stringResource(
                    when (uiState.step) {
                        OnboardingUiState.Step.Welcome -> R.string.onboarding_action_start
                        OnboardingUiState.Step.Interests -> R.string.onboarding_action_continue
                        OnboardingUiState.Step.Notifications -> R.string.onboarding_action_enable
                    }
                ),
                style = MaterialTheme.typography.labelLarge
            )
        }

        // "Skip" is present on every step that asks for something. Onboarding must never
        // be a wall: an empty selection is a valid answer.
        if (uiState.step != OnboardingUiState.Step.Welcome) {
            TextButton(
                onClick = { if (uiState.isLastStep) onFinish() else onNext() },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(stringResource(R.string.onboarding_action_skip))
            }
        }
    }
}

/** The current step's bar widens, so progress is legible without counting dots. */
@Composable
private fun StepIndicator(current: Int, total: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(total) { index ->
            val width by animateDpAsState(
                targetValue = if (index == current) 28.dp else 14.dp,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
                label = "stepWidth"
            )
            Box(
                modifier = Modifier
                    .height(3.dp)
                    .width(width)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index <= current) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingInterestsPreview() = InfotifyTheme {
    OnboardingScreen(
        uiState = OnboardingUiState(step = OnboardingUiState.Step.Interests),
        onToggleTopic = {},
        onToggleRegion = {},
        onNext = {},
        onFinish = {}
    )
}
