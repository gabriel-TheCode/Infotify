package com.thecode.infotify.activities

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.thecode.infotify.R
import com.thecode.infotify.adapters.BottomNavPagerAdapter
import com.thecode.infotify.databinding.ActivityMainBinding
import com.thecode.infotify.fragments.BookmarksFragment
import com.thecode.infotify.fragments.HomeFragment
import com.thecode.infotify.fragments.SearchFragment
import com.thecode.infotify.interfaces.FadePageTransformer
import com.thecode.infotify.utils.SharedPreferenceUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (SharedPreferenceUtils.isNightModeEnabled()) {
            setAppTheme(R.style.AppTheme_Base_Night)
        } else {
            setAppTheme(R.style.AppTheme_Base_Light)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bnlv = binding.bottomNavigationBar
        bnlv.setTypeface(Typeface.createFromAsset(assets, "fonts/SF-Medium.otf"))
        //bnlv.setBadgeValue(0, "9+")

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
