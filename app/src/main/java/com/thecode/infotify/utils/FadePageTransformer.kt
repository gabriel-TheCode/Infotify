package com.thecode.infotify.utils

import android.view.View
import androidx.viewpager.widget.ViewPager


class FadePageTransformer : ViewPager.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.alpha = 0f
        view.visibility = View.VISIBLE

        // Start Animation for a short period of time
        view.animate()
            .alpha(1f).duration =
            view.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    }
}