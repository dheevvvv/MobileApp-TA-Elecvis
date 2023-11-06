package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentWelcomePageBinding
import com.dheevvvv.taelecvis.view.adapter.WelcomePageAdapter
import com.google.android.material.tabs.TabLayoutMediator


class WelcomePageFragment : Fragment() {
    private lateinit var binding: FragmentWelcomePageBinding
    private val mPageNumber = 4
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWelcomePageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = WelcomePageAdapter(this, getListOfPagerContents(), mPageNumber )
        val viewPager = binding.viewPager
        val tabsLayout = binding.tabLayout
        viewPager.adapter = pagerAdapter

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_welcomePageFragment_to_loginFragment)
        }

        tabsLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), com.google.android.material.R.color.mtrl_btn_transparent_bg_color))

        TabLayoutMediator(tabsLayout, viewPager)
        {tab, position ->}.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == pagerAdapter.itemCount - 1){
                    binding.btnNext.visibility = View.VISIBLE
                } else{
                    binding.btnNext.visibility = View.GONE
                }
            }
        })
    }

    fun getListOfPagerContents(): List<Array<String>> {

        val ar1 = arrayOf(getString(R.string.intro_title_1), getString(R.string.intro_sub_title_1),"R" )
        val ar2 = arrayOf(getString(R.string.intro_title_2), getString(R.string.intro_sub_title_2) ,"G")
        val ar3 = arrayOf(getString(R.string.intro_title_3), getString(R.string.intro_sub_title_3) ,"B")
        val ar4 = arrayOf(getString(R.string.intro_title_4), getString(R.string.intro_sub_title_4) ,"D")
        return listOf(ar1,ar2,ar3, ar4)
    }


}