package com.dheevvvv.taelecvis.view.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dheevvvv.taelecvis.view.INTRO_STRING_OBJECT
import com.dheevvvv.taelecvis.view.ViewPagerFragment

class WelcomePageAdapter(fragment: Fragment, val listOfPagerContents: List<Array<String>>, val mPageNumber: Int): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return mPageNumber
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = ViewPagerFragment()

        when(position){
            0 ->
                fragment.arguments = Bundle().apply {
                    putStringArray(INTRO_STRING_OBJECT, listOfPagerContents[0])
                }
            1 ->
                fragment.arguments = Bundle().apply {
                    putStringArray(INTRO_STRING_OBJECT, listOfPagerContents[1])
                }
            2 ->
                fragment.arguments = Bundle().apply {
                    putStringArray(INTRO_STRING_OBJECT, listOfPagerContents[2])
                }
            3 ->
                fragment.arguments = Bundle().apply {
                    putStringArray(INTRO_STRING_OBJECT, listOfPagerContents[3])
                }
            4 ->
                fragment.arguments = Bundle().apply {
                    putStringArray(INTRO_STRING_OBJECT, listOfPagerContents[4])
                }
        }
        return fragment
    }

}