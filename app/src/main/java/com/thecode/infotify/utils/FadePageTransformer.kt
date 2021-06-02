package com.thecode.infotify.utils

import android.view.View
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2

class FadePageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.alpha = 0f
        view.isVisible = true

        // Start Animation for a short period of time
        view.animate()
            .alpha(1f).duration =
            view.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    }
}