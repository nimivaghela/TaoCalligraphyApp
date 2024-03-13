package com.app.taocalligraphy.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager(val mContext: Context, attrs: AttributeSet) :
    ViewPager(mContext, attrs) {

    private var isSwipeEnabled: Boolean = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (isSwipeEnabled)
            return super.onTouchEvent(ev)

        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (isSwipeEnabled)
            return super.onInterceptTouchEvent(ev)

        return false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.isSwipeEnabled = enabled
    }
}