package com.thecode.infotify.presentation.splash

import android.animation.Animator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.thecode.infotify.databinding.ActivitySplashBinding
import com.thecode.infotify.presentation.main.MainActivity
import com.thecode.infotify.presentation.onboarding.OnboardingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var binding: ActivitySplashBinding
    private lateinit var springForce: SpringForce

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val splashLayout = binding.splashLayout

        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                // do stuff
                // Like your Background calls and all
                springForce = SpringForce(0f)
                splashLayout.pivotX = 0f
                splashLayout.pivotY = 0f
                val springAnim = SpringAnimation(splashLayout, DynamicAnimation.ROTATION).apply {
                    springForce.dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
                    springForce.stiffness = SpringForce.STIFFNESS_VERY_LOW
                }
                springAnim.spring = springForce
                springAnim.setStartValue(80f)
                springAnim.addEndListener { _, _, _, _ ->
                    val displayMetrics = DisplayMetrics()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val display = this.display
                        display?.getRealMetrics(displayMetrics)
                    } else {
                        @Suppress("DEPRECATION")
                        val display = this.windowManager.defaultDisplay
                        @Suppress("DEPRECATION")
                        display.getMetrics(displayMetrics)
                    }
                    val height = displayMetrics.heightPixels.toFloat()
                    val width = displayMetrics.widthPixels
                    splashLayout.animate()
                        .setStartDelay(1)
                        .translationXBy(width.toFloat() / 2)
                        .translationYBy(height)
                        .setListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(p0: Animator?) {
                            }

                            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                            override fun onAnimationEnd(p0: Animator?) {
                                val intent: Intent = if (viewModel.isOnboardingCompleted()) {
                                    Intent(applicationContext, MainActivity::class.java)
                                } else {
                                    Intent(applicationContext, OnboardingActivity::class.java)
                                }
                                finish()
                                startActivity(intent)

                                overridePendingTransition(0, 0)
                            }

                            override fun onAnimationCancel(p0: Animator?) {
                            }

                            override fun onAnimationStart(p0: Animator?) {
                            }
                        })
                        .setInterpolator(DecelerateInterpolator(1f))
                        .start()
                }
                springAnim.start()
            },
            4000
        )
    }
}
