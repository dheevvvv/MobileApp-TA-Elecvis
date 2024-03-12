package com.dheevvvv.taelecvis.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.manager.Lifecycle
import com.dheevvvv.taelecvis.view.ElectricInfoFragment
import com.dheevvvv.taelecvis.view.RecomendationFragment

class DetailPagerAdapter(fragment: FragmentManager, lifecycle: androidx.lifecycle.Lifecycle) :
    FragmentStateAdapter(fragment, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ElectricInfoFragment()
            1 -> RecomendationFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }


}