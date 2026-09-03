package com.thecode.infotify.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import com.thecode.infotify.notification.DailyBriefingWorker
import dagger.hilt.android.AndroidEntryPoint

/**
 * The app's only Activity.
 *
 * The system splash stays up only while the stored theme and onboarding flag are read —
 * typically tens of milliseconds, against the 2 500 ms postDelayed plus two animations the
 * original splash imposed.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !viewModel.isReady }
        enableEdgeToEdge()

        // Set when the activity is launched from the daily briefing.
        val openForYou = intent?.getBooleanExtra(DailyBriefingWorker.EXTRA_OPEN_FOR_YOU, false)
            ?: false

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            InfotifyTheme(mode = uiState.themeMode) {
                // Read once, when the first composition happens: the flag must not flip
                // mid-session and yank the user back into onboarding.
                InfotifyRoot(
                    startWithOnboarding = !uiState.onboardingCompleted,
                    openForYou = openForYou
                )
            }
        }
    }
}
