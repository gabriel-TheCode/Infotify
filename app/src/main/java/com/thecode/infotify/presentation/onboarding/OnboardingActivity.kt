package com.thecode.infotify.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.thecode.infotify.R
import com.thecode.infotify.core.domain.OnBoardingState
import com.thecode.infotify.databinding.ActivityOnboardingBinding
import com.thecode.infotify.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private val viewModel: OnboardingViewModel by viewModels()
    private val onBoardingAdapter = OnBoardingAdapter()
    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)

        setUpListeners()
        setUpPager()
        setUpObserver()
        viewModel.getOnBoardingSlide()

        setContentView(binding.root)
    }

    private fun setUpListeners() {
        binding.apply {
            btnNextStep.setOnClickListener {
                if (getNextItem() > getAdapterSize()) {
                    viewModel.setOnboardingCompleted()
                    launchMainScreen()
                } else {
                    viewPager.setCurrentItem(getNextItem(), true)
                }
            }

            btnPreviousStep.setOnClickListener {
                if (getNextItem() == 1) {
                    finish()
                } else {
                    viewPager.setCurrentItem(getPreviousItem(), true)
                }
            }
        }
    }

    private fun setUpPager() {
        binding.apply {
            viewPager.offscreenPageLimit = 1
            viewPager.adapter = onBoardingAdapter
            TabLayoutMediator(pageIndicator, viewPager) { _, _ -> }.attach()
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == 2) {
                        btnNextStep.text = getText(R.string.finish)
                    } else {
                        btnNextStep.text = getText(R.string.next)
                    }
                }
            })
        }
    }

    private fun setUpObserver() {
        viewModel.state.observe(
            this
        ) {
            when (it) {
                is OnBoardingState.Complete -> onBoardingAdapter.setItem(it.list)
            }
        }
    }

    private fun launchMainScreen() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finishAffinity()
    }

    private fun getNextItem(): Int {
        return binding.viewPager.currentItem + 1
    }

    private fun getPreviousItem(): Int {
        return binding.viewPager.currentItem - 1
    }

    private fun getAdapterSize(): Int {
        return onBoardingAdapter.itemCount - 1
    }
}