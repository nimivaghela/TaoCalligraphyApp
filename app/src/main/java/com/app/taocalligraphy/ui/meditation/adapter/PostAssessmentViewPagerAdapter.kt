package com.app.taocalligraphy.ui.meditation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class PostAssessmentViewPagerAdapter(
    fm: FragmentManager,
    private val fragmentList: ArrayList<Fragment>
) : FragmentStatePagerAdapter(fm) {

    override fun getCount() = 2

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }
}