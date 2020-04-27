package com.thecode.infotify.activities

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.thecode.infotify.R
import com.thecode.infotify.adapters.BottomNavPagerAdapter
import com.thecode.infotify.fragments.BookmarksFragment
import com.thecode.infotify.fragments.HomeFragment
import com.thecode.infotify.fragments.SearchFragment
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_main_2.*
import java.util.*

class MainActivity2 : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)

        val bnlv = bottom_navigation_bar
        bnlv.setTypeface(Typeface.createFromAsset(assets, "fonts/SF-Medium.otf"))
        //bnlv.setBadgeValue(0, "3");
        //bnlv.setBadgeValue(1, "9+"); //invisible badge
        //bnlv.setBadgeValue(0, "9+")

        val fragList = ArrayList<Fragment>()
        fragList.add(HomeFragment())
        fragList.add(SearchFragment())
        fragList.add(BookmarksFragment())
        val pagerAdapter = BottomNavPagerAdapter(fragList, supportFragmentManager)
        val viewPager = view_pager
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = pagerAdapter

        bnlv.setNavigationChangeListener { _, position ->
            viewPager.setCurrentItem(
                position,
                true
            )
        }
    }


}
