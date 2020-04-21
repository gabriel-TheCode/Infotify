/*
 * Copyright (c) 2019. Variance technologies. All rights reserved.
 * Created By TEKOMBO Gabriel <tekombo.gabriel@gmail.com> on 07/10/19 13:42.
 */
package com.thecode.infotify.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager

class NonSwipeableViewPager : ViewPager {
    constructor(context: Context?) : super(context!!) {
        setMyScroller()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        setMyScroller()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        // Never allow swiping to switch between pages
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Never allow swiping to switch between pages
        return false
    }

    //down one is added for smooth scrolling
    private fun setMyScroller() {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            scroller[this] = MyScroller(
                context
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class MyScroller internal constructor(context: Context?) :
        Scroller(context, DecelerateInterpolator()) {
        override fun startScroll(
            startX: Int,
            startY: Int,
            dx: Int,
            dy: Int,
            duration: Int
        ) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/)
        }
    }
}