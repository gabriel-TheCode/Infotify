package com.thecode.infotify.presentation.main.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mahfa.dnswitch.DayNightSwitch
import com.mahfa.dnswitch.DayNightSwitchAnimListener
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseFragment
import com.thecode.infotify.presentation.about.AboutActivity
import com.thecode.infotify.presentation.main.headline.HeadlineViewPagerAdapter
import com.thecode.infotify.databinding.FragmentHomeBinding
import com.thecode.infotify.presentation.main.headline.HeadlineFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val TAG = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dayNightSwitch: DayNightSwitch
    private lateinit var mActivity: Activity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        setHasOptionsMenu(true)
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        val mViewPager: ViewPager = binding.viewpager
        val mViewPagerAdapter = HeadlineViewPagerAdapter(requireActivity().supportFragmentManager)
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("popular"), "Popular")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("general"), "General")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("science"), "Science")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("sports"), "Sports")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("technology"), "Technology")
        mViewPagerAdapter.addFragment(HeadlineFragment.newInstance("entertainment"), "Entertainment")
        mViewPager.adapter = mViewPagerAdapter
        mViewPager.offscreenPageLimit = 6

        val tabLayout: TabLayout = binding.tabs
        tabLayout.setupWithViewPager(mViewPager)

        dayNightSwitch = binding.dayNightSwitch
        dayNightSwitch.setDuration(450)
        dayNightSwitch.setAnimListener(object : DayNightSwitchAnimListener {
            override fun onAnimStart() {
                Log.d(TAG, "onAnimStart() called")
            }

            override fun onAnimEnd() {
                Log.d(TAG, "onAnimEnd() called")
                if (viewModel.isNightModeActivated()) {
                    viewModel.setNightMode(false)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    viewModel.setNightMode(true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                // Recreate activity
                mActivity.recreate()
            }

            override fun onAnimValueChanged(value: Float) {}
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        // Get state from preferences
        if (viewModel.isNightModeActivated()) {
            dayNightSwitch.setIsNight(true)
        } else {
            dayNightSwitch.setIsNight(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
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
