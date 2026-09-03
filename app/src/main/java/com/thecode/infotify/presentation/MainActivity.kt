package com.thecode.infotify.presentation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
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
        splashScreen.setOnExitAnimationListener(::animateSplashExit)
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

    /**
     * The splash's exit is where the brand moment actually happens.
     *
     * windowSplashScreenAnimatedIcon only animates an AnimatedVectorDrawable from API 31;
     * below that the compat library shows a still image, and minSdk here is 26. On top of
     * that, setKeepOnScreenCondition releases as soon as the stored theme is read, which is
     * usually before any entry animation could play. So the icon was, in practice, static
     * on most devices — which is what it looked like.
     *
     * Animating the exit instead works on every supported version and costs nothing: it
     * runs while the app is already composed behind it, so it delays no content. The icon
     * lifts and fades as the ground drops away, and the app is revealed underneath.
     */
    private fun animateSplashExit(provider: SplashScreenViewProvider) {
        val icon = provider.iconView
        val root = provider.view

        val lift = ObjectAnimator.ofFloat(icon, View.TRANSLATION_Y, 0f, -icon.height * 0.12f)
        val scaleX = ObjectAnimator.ofFloat(icon, View.SCALE_X, 1f, 1.08f)
        val scaleY = ObjectAnimator.ofFloat(icon, View.SCALE_Y, 1f, 1.08f)
        val fadeIcon = ObjectAnimator.ofFloat(icon, View.ALPHA, 1f, 0f)
        val fadeGround = ObjectAnimator.ofFloat(root, View.ALPHA, 1f, 0f)

        AnimatorSet().apply {
            playTogether(lift, scaleX, scaleY, fadeIcon, fadeGround)
            duration = SPLASH_EXIT_MILLIS
            interpolator = AccelerateInterpolator(1.4f)
            // remove() must run whatever happens: a splash that fails to detach leaves the
            // app unusable behind a frozen image.
            doOnEnd { provider.remove() }
            start()
        }
    }

    private companion object {
        const val SPLASH_EXIT_MILLIS = 340L
    }
}
