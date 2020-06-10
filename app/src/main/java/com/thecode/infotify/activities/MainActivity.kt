package com.thecode.infotify.activities

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.thecode.infotify.R
import com.thecode.infotify.adapters.BottomNavPagerAdapter
import com.thecode.infotify.fragments.BookmarksFragment
import com.thecode.infotify.fragments.HomeFragment
import com.thecode.infotify.fragments.SearchFragment
import com.thecode.infotify.interfaces.FadePageTransformer
import com.thecode.infotify.utils.SharedPreferenceUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_main_2.*
import java.util.*

class MainActivity : AppCompatActivity() {

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

        setContentView(R.layout.activity_main_2)

        val bnlv = bottom_navigation_bar
        bnlv.setTypeface(Typeface.createFromAsset(assets, "fonts/SF-Medium.otf"))
        //bnlv.setBadgeValue(0, "9+")

        val fragList = ArrayList<Fragment>()
        fragList.add(HomeFragment())
        fragList.add(SearchFragment())
        fragList.add(BookmarksFragment())
        val pagerAdapter = BottomNavPagerAdapter(fragList, supportFragmentManager)
        val viewPager = view_pager
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
