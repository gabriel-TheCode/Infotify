package com.thecode.infotify.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.thecode.infotify.R
import com.thecode.infotify.adapters.BottomNavPagerAdapter
import com.thecode.infotify.fragments.BookmarksFragment
import com.thecode.infotify.fragments.HomeFragment
import com.thecode.infotify.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_main_2.*
import java.util.ArrayList

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)

        val bnlv = bottom_navigation_bar
        //bncw.setTypeface(Typeface.createFromAsset(getAssets(), "rubik.ttf"));
        //bnlv.setBadgeValue(0, "3");
        //bnlv.setBadgeValue(1, "9+"); //invisible badge
        //bnlv.setBadgeValue(0, "9+")

        val fragList = ArrayList<Fragment>()
        fragList.add(HomeFragment())
        fragList.add(BookmarksFragment())
        fragList.add(SearchFragment())
        val pagerAdapter = BottomNavPagerAdapter(fragList, supportFragmentManager)
        val viewPager = view_pager
        viewPager.adapter = pagerAdapter

        bnlv.setNavigationChangeListener { _, position ->
            viewPager.setCurrentItem(
                position,
                true
            )
        }
    }


}
