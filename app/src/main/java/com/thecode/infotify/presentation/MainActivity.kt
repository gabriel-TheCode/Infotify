package com.thecode.infotify.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.thecode.infotify.designsystem.theme.InfotifyTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The app's only Activity.
 *
 * Replaces SplashActivity, MainActivity, OnboardingActivity, AboutActivity,
 * LanguageActivity and NewsDetailsActivity. The system splash stays up only while the
 * stored theme is read — typically tens of milliseconds, against the 2 500 ms
 * postDelayed plus two animations the previous splash imposed.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Hold the splash until the theme is known, so the app never paints the wrong one first.
        splashScreen.setKeepOnScreenCondition { !viewModel.isReady }

        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            InfotifyTheme(mode = uiState.themeMode) {
                InfotifyRoot()
            }
        }
    }
}
