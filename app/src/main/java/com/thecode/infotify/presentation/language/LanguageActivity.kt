package com.thecode.infotify.presentation.language

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.viewModels
import com.thecode.infotify.R
import com.thecode.infotify.base.BaseActivity
import com.thecode.infotify.databinding.ActivityLanguageBinding
import com.thecode.infotify.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : BaseActivity(), Animation.AnimationListener {

    private lateinit var binding: ActivityLanguageBinding
    private val languageViewModel: LanguageViewModel by viewModels()
    private lateinit var languagesAdapter: ArrayAdapter<String>
    private lateinit var animFadeIn: Animation

    private lateinit var btnContinue: Button
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initViews()
        setUpAnimation()
        setUpAdapter()

        languageViewModel.languages.observe(this, { displayLanguages(it) })

        languageViewModel.getCurrentLanguages()

    }

    private fun initViews(){
        btnContinue = binding.continueButton
        spinner = binding.preferredLanguageSpinner

        btnContinue.setOnClickListener {
            onClickNext()
        }
    }

    private fun displayLanguages(languages: List<String>) {
        languagesAdapter.clear()
        languagesAdapter.addAll(languages)
    }

    private fun setUpAdapter() {
        languagesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.apply {
            adapter = languagesAdapter
            setSelection(0, false)
            gravity = Gravity.CENTER

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    position: Int,
                    l: Long
                ) {
                    onLanguageSelected(position)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        }
    }

    private fun setUpAnimation() {
        animFadeIn = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.animate_fade_enter
        )
        animFadeIn.setAnimationListener(this)
    }

    private fun onClickNext() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_from_right)
        startSingleTopActivity(intent)
        finish()
    }

    private fun onLanguageSelected(position: Int) {
        languageViewModel.saveLanguagePref(position)
    }

    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {}
    override fun onAnimationRepeat(animation: Animation) {}
}