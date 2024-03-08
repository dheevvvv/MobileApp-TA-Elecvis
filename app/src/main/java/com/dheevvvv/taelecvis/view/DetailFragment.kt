package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dheevvvv.taelecvis.databinding.FragmentDetailBinding
import com.dheevvvv.taelecvis.view.adapter.DetailPagerAdapter


class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayoutDetail
        val viewPager = binding.viewPagerDetail
        val tabTitlesList = listOf("Description Info", "Recommendation")

        val fragments = listOf(ElectricInfoFragment(), RecomendationFragment())
        val adapter = DetailPagerAdapter(childFragmentManager, fragments, tabTitlesList)
        viewPager.adapter = adapter


        tabLayout.setupWithViewPager(viewPager)
    }


}