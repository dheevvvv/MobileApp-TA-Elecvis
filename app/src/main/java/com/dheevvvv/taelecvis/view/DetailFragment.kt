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
            1 -> TrenKonsumsiHarian()
            2 -> PeakEnergyKonsumsi()
            3 -> Voltage()
            4 -> Intensitas()
            5 -> Submeter()
        }
}

    @SuppressLint("SetTextI18n")
    private fun TrenKonsumsiHarian(){
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

                        //overview
                        // Inisialisasi nilai tertinggi dan terendah
                        var highestValue = Float.MIN_VALUE
                        var lowestValue = Float.MAX_VALUE

                        // Inisialisasi total dan jumlah entri
                        var totalValue = 0f
                        var entryCount = 0

                        // Iterasi melalui set data dalam LineData
                        for (i in 0 until lineData.dataSetCount) {
                            val dataSet = lineData.getDataSetByIndex(i)

                            // Iterasi melalui semua entri dalam set data
                            for (j in 0 until dataSet.entryCount) {
                                val entry = dataSet.getEntryForIndex(j)
                                val yValue = entry.y

                                // Perbarui nilai tertinggi dan terendah
                                if (yValue > highestValue) {
                                    highestValue = yValue
                                }
                                if (yValue < lowestValue) {
                                    lowestValue = yValue
                                }

                                // Tambahkan nilai ke total
                                totalValue += yValue
                                entryCount++
                            }
                        }
                        val averageValue = totalValue / entryCount

                        binding.tvHighValue.setText("$highestValue" + " " + "kWh")
                        binding.tvLowValue.setText("$lowestValue" + " " + "kWh")
                        binding.tvAverageValue.setText("$averageValue" + " " + "kWh")
                        binding.tvTotalValue.setText("$totalValue" + " " + "kWh")


                    } else {
                        Toast.makeText(requireContext(), "Labels null", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireActivity(), "Line data null", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun PeakEnergyKonsumsi(){
        homeViewModel.dataChartPeakKonsumsi.observe(viewLifecycleOwner, Observer { data ->
            if (data != null) {
                homeViewModel.labelsPeakKonsumsi.observe(viewLifecycleOwner, Observer { labels ->
                    if (labels != null) {
                        val barChart = binding.barChart
                        val barData = data
                        val xAxisLabels = labels
                        barChart.data = barData
                        homeViewModel.updateChartDataPeakKonsumsi(barData)

                        val description = Description()
                        description.text = "Peak Consumption Trend"
                        barChart.description = description

                        // Pengaturan sumbu X
                        val xAxis = barChart.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM

                        // Pengaturan sumbu Y
                        val yAxis = barChart.axisLeft
                        yAxis.axisMinimum = 0f

                        barChart.invalidate()
                        binding.tvTitleDetail.text = "Peak Energy Konsumsi"
                        binding.mcBar.visibility = View.GONE

                        //overview
                        // Inisialisasi nilai tertinggi dan terendah
                        var highestValue = Float.MIN_VALUE
                        var lowestValue = Float.MAX_VALUE

                        // Inisialisasi total dan jumlah entri
                        var totalValue = 0f
                        var entryCount = 0

                        // Iterasi melalui set data dalam LineData
                        for (i in 0 until barData.dataSetCount) {
                            val dataSet = barData.getDataSetByIndex(i)

                            // Iterasi melalui semua entri dalam set data
                            for (j in 0 until dataSet.entryCount) {
                                val entry = dataSet.getEntryForIndex(j)
                                val yValue = entry.y

                                // Perbarui nilai tertinggi dan terendah
                                if (yValue > highestValue) {
                                    highestValue = yValue
                                }
                                if (yValue < lowestValue) {
                                    lowestValue = yValue
                                }

                                // Tambahkan nilai ke total
                                totalValue += yValue
                                entryCount++
                            }
                        }
                        val averageValue = totalValue / entryCount

                        binding.tvHighValue.setText("$highestValue" + " " + "kWh")
                        binding.tvLowValue.setText("$lowestValue" + " " + "kWh")
                        binding.tvAverageValue.setText("$averageValue" + " " + "kWh")
                        binding.tvTotalValue.setText("$totalValue" + " " + "kWh")


                    } else {
                        Toast.makeText(requireContext(), "Labels null", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireActivity(), " data null", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun Voltage(){
        homeViewModel.dataChartVoltage.observe(viewLifecycleOwner, Observer { data ->
            if (data != null) {
                homeViewModel.labelsVoltage.observe(viewLifecycleOwner, Observer { labels ->
                    if (labels != null) {
                        val barChart = binding.barChart
                        val barData = data
                        val xAxisLabels = labels
                        barChart.data = barData
                        homeViewModel.updateChartDataVoltage(barData)

                        val description = Description()
                        description.text = "Voltage"
                        barChart.description = description

                        // Pengaturan sumbu X
                        val xAxis = barChart.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM

                        // Pengaturan sumbu Y
                        val yAxis = barChart.axisLeft
                        yAxis.axisMinimum = 0f

                        barChart.invalidate()
                        binding.tvTitleDetail.text = "Voltage"
                        binding.mcBar.visibility = View.GONE

                        //overview
                        // Inisialisasi nilai tertinggi dan terendah
                        var highestValue = Float.MIN_VALUE
                        var lowestValue = Float.MAX_VALUE

                        // Inisialisasi total dan jumlah entri
                        var totalValue = 0f
                        var entryCount = 0

                        // Iterasi melalui set data dalam LineData
                        for (i in 0 until barData.dataSetCount) {
                            val dataSet = barData.getDataSetByIndex(i)

                            // Iterasi melalui semua entri dalam set data
                            for (j in 0 until dataSet.entryCount) {
                                val entry = dataSet.getEntryForIndex(j)
                                val yValue = entry.y

                                // Perbarui nilai tertinggi dan terendah
                                if (yValue > highestValue) {
                                    highestValue = yValue
                                }
                                if (yValue < lowestValue) {
                                    lowestValue = yValue
                                }

                                // Tambahkan nilai ke total
                                totalValue += yValue
                                entryCount++
                            }
                        }
                        val averageValue = totalValue / entryCount

                        binding.tvHighValue.setText("$highestValue" + " " + "V (volt)")
                        binding.tvLowValue.setText("$lowestValue" + " " + "V (volt)")
                        binding.tvAverageValue.setText("$averageValue" + " " + "V (volt)")
                        binding.tvTotalValue.setText("$totalValue" + " " + "V (volt)")


                    } else {
                        Toast.makeText(requireContext(), "Labels null", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireActivity(), " data null", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun Intensitas(){
        homeViewModel.dataChartIntensitas.observe(viewLifecycleOwner, Observer { data ->
            if (data != null) {
                homeViewModel.labelsIntensitas.observe(viewLifecycleOwner, Observer { labels ->
                    if (labels != null) {
                        val barChart = binding.barChart
                        val barData = data
                        val xAxisLabels = labels
                        barChart.data = barData
                        homeViewModel.updateChartDataIntensitas(barData)

                        val description = Description()
                        description.text = "Global Intensity"
                        barChart.description = description

                        // Pengaturan sumbu X
                        val xAxis = barChart.xAxis
                        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                        xAxis.position = XAxis.XAxisPosition.BOTTOM

                        // Pengaturan sumbu Y
                        val yAxis = barChart.axisLeft
                        yAxis.axisMinimum = 0f

                        barChart.invalidate()
                        binding.tvTitleDetail.text = "Global Intensity"
                        binding.mcBar.visibility = View.GONE

                        //overview
                        // Inisialisasi nilai tertinggi dan terendah
                        var highestValue = Float.MIN_VALUE
                        var lowestValue = Float.MAX_VALUE

                        // Inisialisasi total dan jumlah entri
                        var totalValue = 0f
                        var entryCount = 0

                        // Iterasi melalui set data dalam LineData
                        for (i in 0 until barData.dataSetCount) {
                            val dataSet = barData.getDataSetByIndex(i)

                            // Iterasi melalui semua entri dalam set data
                            for (j in 0 until dataSet.entryCount) {
                                val entry = dataSet.getEntryForIndex(j)
                                val yValue = entry.y

                                // Perbarui nilai tertinggi dan terendah
                                if (yValue > highestValue) {
                                    highestValue = yValue
                                }
                                if (yValue < lowestValue) {
                                    lowestValue = yValue
                                }

                                // Tambahkan nilai ke total
                                totalValue += yValue
                                entryCount++
                            }
                        }
                        val averageValue = totalValue / entryCount

                        binding.tvHighValue.setText("$highestValue" + " " + "A (ampere)")
                        binding.tvLowValue.setText("$lowestValue" + " " + "A (ampere)")
                        binding.tvAverageValue.setText("$averageValue" + " " + "A (ampere)")
                        binding.tvTotalValue.setText("$totalValue" + " " + "A (ampere)")


                    } else {
                        Toast.makeText(requireContext(), "Labels null", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireActivity(), "data null", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun Submeter(){
        homeViewModel.dataChartSubmeter.observe(viewLifecycleOwner, Observer { data ->
            if (data != null) {
                homeViewModel.labelsSubmeter.observe(viewLifecycleOwner, Observer { labels ->
                    if (labels != null) {
                        val pieChart = binding.pieChart
                        val pieData = data
                        val xAxisLabels = labels
                        pieChart.data = pieData
                        homeViewModel.updateChartDataSubmeter(pieData)

                        // Set description for the chart
                        val description = Description()
                        description.text = "Sub-Metering Composition"
                        pieChart.description = description

                        // Set labels for the chart
                        pieChart.setDrawMarkers(true)


                        // Set data for the chart
                        pieChart.data = pieData

                        // Invalidate and refresh the chart
                        pieChart.invalidate()
                        binding.tvTitleDetail.text = "Peak Energy Konsumsi"
                        binding.mcBar.visibility = View.GONE

                        //overview
                        // Inisialisasi nilai tertinggi dan terendah
                        var highestValue = Float.MIN_VALUE
                        var lowestValue = Float.MAX_VALUE

                        // Inisialisasi total dan jumlah entri
                        var totalValue = 0f
                        var entryCount = 0

                        // Iterasi melalui set data dalam LineData
                        for (i in 0 until pieData.dataSetCount) {
                            val dataSet = pieData.getDataSetByIndex(i)

                            // Iterasi melalui semua entri dalam set data
                            for (j in 0 until dataSet.entryCount) {
                                val entry = dataSet.getEntryForIndex(j)
                                val yValue = entry.y

                                // Perbarui nilai tertinggi dan terendah
                                if (yValue > highestValue) {
                                    highestValue = yValue
                                }
                                if (yValue < lowestValue) {
                                    lowestValue = yValue
                                }

                                // Tambahkan nilai ke total
                                totalValue += yValue
                                entryCount++
                            }
                        }
                        val averageValue = totalValue / entryCount

                        binding.tvHighValue.setText("$highestValue" + " " + "Watt")
                        binding.tvLowValue.setText("$lowestValue" + " " + "Watt")
                        binding.tvAverageValue.setText("$averageValue" + " " + "Watt")
                        binding.tvTotalValue.setText("$totalValue" + " " + "Watt")


                    } else {
                        Toast.makeText(requireContext(), "Labels null", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(requireActivity(), "data null", Toast.LENGTH_SHORT).show()
            }
        })

    }

}