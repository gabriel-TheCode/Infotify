package com.thecode.infotify.activities

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thecode.infotify.R
import com.thecode.infotify.adapters.BottomNavPagerAdapter
import com.thecode.infotify.fragments.BookmarksFragment
import com.thecode.infotify.fragments.HomeFragment
import com.thecode.infotify.fragments.SearchFragment
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewPager: ViewPager

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bnv = bottom_navigation
        val fragList = ArrayList<Fragment>()
        fragList.add(HomeFragment())
        fragList.add(SearchFragment())
        fragList.add(BookmarksFragment())
        val pagerAdapter = BottomNavPagerAdapter(fragList, supportFragmentManager)
        viewPager = findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = pagerAdapter

        bnv.setOnNavigationItemSelectedListener(this)

    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.home -> viewPager.currentItem = 0
            R.id.bookmark -> viewPager.currentItem = 1
            R.id.search -> viewPager.currentItem = 2
        }
        return true
    }
}
