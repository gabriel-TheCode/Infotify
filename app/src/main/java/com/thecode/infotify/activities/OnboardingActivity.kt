package com.thecode.infotify.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.viewpager.widget.ViewPager
import com.thecode.infotify.R
import com.thecode.infotify.adapters.OnboardingViewPagerAdapter
import com.thecode.infotify.databinding.ActivityOnboardingBinding
import com.thecode.infotify.utils.SharedPreferenceUtils

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private lateinit var mViewPager: ViewPager
    private lateinit var btnBack: Button
    private lateinit var btnNext: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mViewPager = binding.viewPager
        mViewPager.adapter = OnboardingViewPagerAdapter(supportFragmentManager, this)
        mViewPager.offscreenPageLimit = 1
        btnBack = binding.btnPreviousStep
        btnNext = binding.btnNextStep
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                if(position == 2){
                    btnNext.text = getText(R.string.finish)
                }else{
                    btnNext.text = getText(R.string.next)
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })

        btnNext.setOnClickListener {
            if (getItem(+1) > mViewPager.childCount-1) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                finish()
                startActivity(intent)
                SharedPreferenceUtils.setIsOnboardingCompleted()
            } else {
                mViewPager.setCurrentItem(getItem(+1), true)
            }
        }

            btnBack.setOnClickListener {
            if (getItem(+1) == 1) {
                finish()
            } else {
                mViewPager.setCurrentItem(getItem(-1), true)
            }
        }
    }

    private fun getItem(i: Int): Int {
        return mViewPager.currentItem + i
    }
}
