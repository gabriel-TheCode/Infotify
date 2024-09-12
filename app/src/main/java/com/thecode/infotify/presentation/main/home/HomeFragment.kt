package com.thecode.infotify.presentation.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.mahfa.dnswitch.DayNightSwitch
import com.mahfa.dnswitch.DayNightSwitchAnimListener
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseFragment
import com.thecode.infotify.databinding.FragmentHomeBinding
import com.thecode.infotify.presentation.about.AboutActivity
import com.thecode.infotify.presentation.main.headline.HeadlineFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dayNightSwitch: DayNightSwitch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        viewModel.fetchNightMode()

        dayNightSwitch = binding.dayNightSwitch
        dayNightSwitch.setDuration(450)
        dayNightSwitch.setAnimListener(object : DayNightSwitchAnimListener {
            override fun onAnimValueChanged(value: Float) = Unit

            override fun onAnimStart() = Unit

            override fun onAnimEnd() {
                val isNightMode = dayNightSwitch.isNight
                viewModel.setNightMode(isNightMode)
            }
        })

        initOptionsMenu()
        observeNightMode()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val childFragment: Fragment = HeadlineFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment, childFragment)
            .commit()
    }

    private fun observeNightMode() {
        // Observing night mode state from ViewModel
        viewModel.isNightModeState.observe(viewLifecycleOwner) { isNightMode ->
            val nightMode = if (isNightMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            // Set local night mode for the fragment or activity without recreating
            AppCompatDelegate.setDefaultNightMode(nightMode)

            // Sync the switch with the current night mode state
            dayNightSwitch.setIsNight(isNightMode)
        }
    }

    private fun initOptionsMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.about -> {
                        startActivity(Intent(activity, AboutActivity::class.java))
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}
