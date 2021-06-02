package com.thecode.infotify.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseActivity
import com.thecode.infotify.core.domain.OnBoardingState
import com.thecode.infotify.databinding.ActivityOnboardingBinding
import com.thecode.infotify.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : BaseActivity() {

    private val viewModel: OnboardingViewModel by viewModels()
    private val onBoardingAdapter = OnBoardingAdapter()
    private lateinit var binding: ActivityOnboardingBinding

    private lateinit var mViewPager: ViewPager2
    private lateinit var btnBack: Button
    private lateinit var btnNext: Button
    private lateinit var pageIndicator: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initViews()
        setUpListener()
        setUpPager()

        viewModel.state.observe(this, {
            when (it) {
                is OnBoardingState.COMPLET -> onBoardingAdapter.setItem(it.list)
            }
        })
        viewModel.getOnBoardingSlide()
    }

    private fun initViews() {
        mViewPager = binding.viewPager
        mViewPager.offscreenPageLimit = 1

        btnBack = binding.btnPreviousStep
        btnNext = binding.btnNextStep
        pageIndicator = binding.pageIndicator
    }

    private fun setUpListener() {

        btnNext.setOnClickListener {
            if (getNextItem() > getAdapterSize()) {
                launchMainScreen()
            } else {
                mViewPager.setCurrentItem(getNextItem(), true)
            }
        }

        btnBack.setOnClickListener {
            if (getNextItem() == 1) {
                finish()
            } else {
                mViewPager.setCurrentItem(getPreviousItem(), true)
            }
        }
    }

    private fun setUpPager() {
        mViewPager.adapter = onBoardingAdapter
        TabLayoutMediator(pageIndicator, mViewPager) { _, _ -> }.attach()
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 2) {
                    btnNext.text = getText(R.string.finish)
                } else {
                    btnNext.text = getText(R.string.next)
                }
            }
        })
    }

    private fun launchMainScreen() {
        viewModel.setOnboardingCompleted()
        val intent = Intent(applicationContext, MainActivity::class.java)
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_right)
        startSingleTopActivity(intent)
        finishAffinity()
    }

    private fun getNextItem(): Int {
        return mViewPager.currentItem + 1
    }

    private fun getPreviousItem(): Int {
        return mViewPager.currentItem - 1
    }

    private fun getAdapterSize(): Int {
        return onBoardingAdapter.itemCount - 1
    }

}
