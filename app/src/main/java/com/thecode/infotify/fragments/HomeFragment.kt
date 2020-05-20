package com.thecode.infotify.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mahfa.dnswitch.DayNightSwitch
import com.mahfa.dnswitch.DayNightSwitchAnimListener
import com.thecode.infotify.R
import com.thecode.infotify.activities.MainActivity2
import com.thecode.infotify.adapters.HeadlineViewPagerAdapter
import com.thecode.infotify.utils.SharedPreferenceUtils.isNightModeEnabled
import com.thecode.infotify.utils.SharedPreferenceUtils.setIsNightModeEnabled
import kotlinx.android.synthetic.main.fragment_home.view.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    val TAG = "HomeFragment"
    private val KEY_DAY_NIGHT_SWITCH_STATE = "day_night_switch_state"

    private lateinit var dayNightSwitch: DayNightSwitch


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (isNightModeEnabled()) {
            setAppTheme(R.style.AppTheme_Base_Night)
        } else {
            setAppTheme(R.style.AppTheme_Base_Light)
        }

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
        dayNightSwitch.setListener { is_night ->
            Log.d(TAG, "onSwitch() called with: is_night = [$is_night]")
            //if (is_night) layoutHome.alpha = 1f
        }

        dayNightSwitch.setAnimListener(object : DayNightSwitchAnimListener {
            override fun onAnimStart() {
                Log.d(TAG, "onAnimStart() called")
            }

            override fun onAnimEnd() {
                Log.d(TAG, "onAnimEnd() called")
            }

            override fun onAnimValueChanged(value: Float) {
                if (isNightModeEnabled()) {
                    setIsNightModeEnabled(false)
                    //==> setAppTheme(R.style.AppTheme_Base_Light)
                } else {
                    setIsNightModeEnabled(true)
                    //==>setAppTheme(R.style.AppTheme_Base_Night)
                }

                // Recreate activity
                //==>(activity as AppCompatActivity).recreate()
            }
        })

        if (savedInstanceState != null
            && savedInstanceState.containsKey(KEY_DAY_NIGHT_SWITCH_STATE))
            dayNightSwitch.setIsNight(savedInstanceState.getBoolean(KEY_DAY_NIGHT_SWITCH_STATE))


        return view

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_DAY_NIGHT_SWITCH_STATE, dayNightSwitch.isNight)
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
            R.id.about ->
                // Not implemented here
                return false
        }
        return false
    }


    private fun setAppTheme(@StyleRes style: Int) {
        (activity as AppCompatActivity).setTheme(style)
    }




}
