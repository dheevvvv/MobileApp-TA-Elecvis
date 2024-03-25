package com.dheevvvv.taelecvis.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentDetailBinding
import com.dheevvvv.taelecvis.view.adapter.DetailPagerAdapter
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

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
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val idChartData = arguments?.getInt("dataChartId")


        displayChart(idChartData)


        val tabLayout = binding.tabLayoutDetail
        val viewPager2 = binding.viewPagerDetail
        val adapter = DetailPagerAdapter(childFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "Info"
                1 -> "Recommendation"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }.attach()

//        tabLayout.addTab(tabLayout.newTab().setText("Info"))
//        tabLayout.addTab(tabLayout.newTab().setText("Recommendation"))




//        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                viewPager2.currentItem = tab!!.position
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                //
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                //
//            }
//
//        })

        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                tabLayout.selectTab(tabLayout.getTabAt(position))
                val navController = findNavController()
                when (position) {
                    0 -> navController.navigate(R.id.electricInfoFragment)
                    1 -> navController.navigate(R.id.recomendationFragment)
                }
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun displayChart(idChartData: Int?) {
        when (idChartData) {
            1 -> {
                homeViewModel.dataChartTrenKonsumsiHarian.observe(viewLifecycleOwner, Observer { data ->
                    if (data != null) {
                        homeViewModel.labelsTrenKonsumsiHarian.observe(viewLifecycleOwner, Observer { labels ->
                            if (labels != null) {
                                val lineChart = binding.lineChart
                                val lineData = data
                                val xAxisLabels = labels
                                lineChart.data = lineData
                                homeViewModel.updateChartDataTrenKonsumsiHarian(lineData)

                                val description = Description()
                                description.text = "Daily Consumption Trend"
                                lineChart.description = description

                                // Pengaturan sumbu X
                                val xAxis = lineChart.xAxis
                                xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                                xAxis.position = XAxis.XAxisPosition.BOTTOM

                                // Pengaturan sumbu Y
                                val yAxis = lineChart.axisLeft
                                yAxis.axisMinimum = 0f

                                lineChart.invalidate()
                                binding.tvTitleDetail.text = "Tren Konsumsi Harian"
                                binding.mcBar.visibility = View.GONE
                            } else {
                                Toast.makeText(requireContext(), "Labels null", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Toast.makeText(requireActivity(), "Line data null", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else -> {
                Toast.makeText(requireActivity(), "ID chart null", Toast.LENGTH_SHORT).show()
            }
        }
    }



}