package com.thecode.infotify.presentation.main

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.thecode.infotify.R
import com.thecode.infotify.databinding.ActivityMainBinding
import com.thecode.infotify.presentation.main.bookmark.BookmarksFragment
import com.thecode.infotify.presentation.main.home.HomeFragment
import com.thecode.infotify.presentation.main.home.HomeViewModel
import com.thecode.infotify.presentation.main.search.SearchFragment
import com.thecode.infotify.utils.FadePageTransformer
import dagger.hilt.android.AndroidEntryPoint
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.isNightModeActivated()) {
            setAppTheme(R.style.AppTheme_Base_Night)
        } else {
            setAppTheme(R.style.AppTheme_Base_Light)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bnlv = binding.bottomNavigationBar
        // bnlv.setBadgeValue(0, "9+")

        val fragList = ArrayList<Fragment>()
        fragList.add(HomeFragment())
        fragList.add(SearchFragment())
        fragList.add(BookmarksFragment())
        val pagerAdapter = BottomNavPagerAdapter(fragList, supportFragmentManager)
        val viewPager = binding.viewPager
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = pagerAdapter
        viewPager.setPageTransformer(false, FadePageTransformer())

        bnlv.setNavigationChangeListener { _, position ->
            viewPager.setCurrentItem(
                position,
                true
            )
        }
    }

    private fun setAppTheme(@StyleRes style: Int) {
        setTheme(style)
    }
}
