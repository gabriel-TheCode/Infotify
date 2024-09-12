package com.thecode.infotify.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.thecode.infotify.R
import com.thecode.infotify.databinding.ActivityMainBinding
import com.thecode.infotify.utils.FadePageTransformer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private lateinit var pagerAdapter: BottomNavPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.isNightModeActivated()) {
            setTheme(R.style.AppTheme_Base_Night)
        } else {
            setTheme(R.style.AppTheme_Base_Light)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        initViews()
        setUpPager()
        setContentView(binding.root)
    }

    private fun initViews() {
        binding.apply {
            bottomNavigationBar.setBadgeValue(0, "9+")
            bottomNavigationBar.setNavigationChangeListener { _, position ->
                viewPager.setCurrentItem(
                    position,
                    true
                )
                bottomNavigationBar.setBadgeValue(0, null)
            }
        }

        pagerAdapter = BottomNavPagerAdapter(this)
    }

    private fun setUpPager() {
        binding.viewPager.apply {
            offscreenPageLimit = 3
            adapter = pagerAdapter
            isUserInputEnabled = false
            setPageTransformer(FadePageTransformer())
        }
    }
}
