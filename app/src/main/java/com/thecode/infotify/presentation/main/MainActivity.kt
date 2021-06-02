package com.thecode.infotify.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gauravk.bubblenavigation.BubbleNavigationLinearView
import com.thecode.infotify.R
import com.thecode.infotify.databinding.ActivityMainBinding
import com.thecode.infotify.utils.FadePageTransformer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private lateinit var bnlv: BubbleNavigationLinearView
    private lateinit var pagerAdapter: BottomNavPagerAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.isNightModeActivated()) {
            setAppTheme(R.style.AppTheme_Base_Night)
        } else {
            setAppTheme(R.style.AppTheme_Base_Light)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        initViews()
        setUpPager()

        setContentView(view)
    }

    private fun setAppTheme(@StyleRes style: Int) {
        setTheme(style)
    }


    private fun initViews() {
        viewPager = binding.viewPager
        bnlv = binding.bottomNavigationBar
        // bnlv.setBadgeValue(0, "9+")
        bnlv.setNavigationChangeListener { _, position ->
            viewPager.setCurrentItem(
                position,
                true
            )
        }
        pagerAdapter = BottomNavPagerAdapter(this)
    }

    private fun setUpPager() {
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false
        viewPager.setPageTransformer(FadePageTransformer())
    }
}
