package com.thecode.infotify.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mahfa.dnswitch.DayNightSwitch
import com.mahfa.dnswitch.DayNightSwitchAnimListener
import com.thecode.infotify.R
import com.thecode.infotify.activities.AboutActivity
import com.thecode.infotify.adapters.HeadlineViewPagerAdapter
import com.thecode.infotify.utils.SharedPreferenceUtils.isNightModeEnabled
import com.thecode.infotify.utils.SharedPreferenceUtils.setIsNightModeEnabled
import kotlinx.android.synthetic.main.fragment_home.view.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private val TAG = "HomeFragment"

    private lateinit var dayNightSwitch: DayNightSwitch
    private lateinit var mActivity: Activity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        val toolbar: Toolbar = view.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

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

        dayNightSwitch = view.day_night_switch
        dayNightSwitch.setDuration(450)
        dayNightSwitch.setAnimListener(object : DayNightSwitchAnimListener {
            override fun onAnimStart() {
                Log.d(TAG, "onAnimStart() called")
            }

            override fun onAnimEnd() {
                Log.d(TAG, "onAnimEnd() called")
                if (isNightModeEnabled()) {
                    setIsNightModeEnabled(false)
                } else {
                    setIsNightModeEnabled(true)
                }
                // Recreate activity
                mActivity.recreate()
            }

            override fun onAnimValueChanged(value: Float) { }
        })

        return view

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        // Get state from preferences
        if (isNightModeEnabled()) {
            dayNightSwitch.setIsNight(true)
        } else {
            dayNightSwitch.setIsNight(false)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about ->{
                val intent = Intent(activity, AboutActivity::class.java)
                startActivity(intent)
            }

        }
        return false
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context
        }
    }

}
