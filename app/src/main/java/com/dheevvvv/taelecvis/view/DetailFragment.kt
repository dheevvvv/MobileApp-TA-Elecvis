package com.dheevvvv.taelecvis.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.databinding.FragmentDetailBinding
import com.dheevvvv.taelecvis.model.power_usage.Data
import com.dheevvvv.taelecvis.view.adapter.DetailPagerAdapter
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

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




//        val tabLayout = binding.tabLayoutDetail
//        val viewPager = binding.viewPagerDetail
//        val tabTitlesList = listOf("Description Info", "Recommendation")
//
//        val fragments = listOf(ElectricInfoFragment(), RecomendationFragment())
//        val adapter = DetailPagerAdapter(requireFragmentManager(), fragments, tabTitlesList)
//        viewPager.adapter = adapter
//
//
//        tabLayout.setupWithViewPager(viewPager)
    }

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