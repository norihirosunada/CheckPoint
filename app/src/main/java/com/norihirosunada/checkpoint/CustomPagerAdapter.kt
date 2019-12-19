package com.norihirosunada.checkpoint

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class CustomPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> { return TabFragment() }
            else ->  { return TabFragment() }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> { return "tab_01" }
            else ->  { return "tab_02" }
        }
    }

    override fun getCount(): Int {
        return 2
    }
}