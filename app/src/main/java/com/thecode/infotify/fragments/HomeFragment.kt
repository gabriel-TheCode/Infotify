package com.thecode.infotify.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.thecode.infotify.R
import com.thecode.infotify.adapters.HeadlineViewPagerAdapter
import kotlinx.android.synthetic.main.app_bar.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val mViewPager: ViewPager = view.viewpager
        val mViewPagerAdapter = HeadlineViewPagerAdapter(activity!!.supportFragmentManager)
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("popular"), "Popular")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("general"), "General")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("science"), "Science")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("sports"), "Sports")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("technology"), "Technology")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("entertainment"), "Entertainment")
        mViewPager.adapter = mViewPagerAdapter
        mViewPager.offscreenPageLimit = 6

        val tabLayout: TabLayout = view.tabs
        tabLayout.setupWithViewPager(mViewPager)

        return view

    }

}
