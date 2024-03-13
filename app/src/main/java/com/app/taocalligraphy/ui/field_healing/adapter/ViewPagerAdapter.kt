package com.app.taocalligraphy.ui.field_healing.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.taocalligraphy.ui.field_healing.fragment.*

public class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return PhysicalBodyFragment()
            1 -> return InternalOrgansFragment()
            2 -> return EmotionsFragment()
            3 -> return RelationshipsFragment()
        }
        return BodyChannelsFragment()
    }
}