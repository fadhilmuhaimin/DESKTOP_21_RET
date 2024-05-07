package com.lksprovinsi.desktop_21.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class CustomFragmentAdapter(fragmentActivity: FragmentManager, lifecycle: Lifecycle, val list: List<Fragment>) : FragmentStateAdapter(fragmentActivity, lifecycle) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }
}