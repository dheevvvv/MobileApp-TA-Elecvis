package com.dheevvvv.taelecvis.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentHomeBinding
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import com.dheevvvv.taelecvis.model.power_usage.Data
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel
import com.dheevvvv.taelecvis.viewmodel.UserViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val userViewModel: UserViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.bottomNavigation.setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.account -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    false
                }

                R.id.recommendation -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    false
                }

                R.id.report -> {
                    findNavController().navigate(R.id.action_homeFragment_to_reportFragment)
                    false
                }

                R.id.notification -> {
                    findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
                    false
                }

                else -> true
            }
        }

        userViewModel.getUsername()
        userViewModel.username.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                val username = it
                binding.tvWelcome.setText("Welcome Back!, ${username}")
            }
        })
        userViewModel.getUserId()

        //Tren Konsumsi Energi
        userViewModel.userId.observe(viewLifecycleOwner, Observer {userId->
            if (userId!=null){
                homeViewModel.callApiGetPowerUsage(userId = userId, startDate = "2007-12-20", endDate = "2007-12-26")
                homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer {
                    if (it!=null){
//                        Log.e("fetch daily consumption", "success")
//                        Log.e("data daily consumption", "${it.size}")
                        val lineChart = binding.lineChart
                        showDailyConsumptionTrend(lineChart, it, startDate = "2007-12-20", endDate = "2007-12-26")

                    } else{
                        Log.e("fetch daily consumption", "null")
                    }
                })
            }
        })

        //Puncak Konsumsi Energi
        userViewModel.userId.observe(viewLifecycleOwner, Observer {userId->
            if (userId!=null){
                homeViewModel.callApiGetPowerUsage(userId = userId, startDate = "2007-12-20", endDate = "2007-12-20")
                homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer {
                    if (it!=null){
//                        Log.e("fetch Peak", "success")
//                        Log.e("data Peak", "${it.size}")
                        val barChartPuncakKonsumsi = binding.chartPuncakKonsumsiEnergi
                        showPeakConsumptionTrend(barChartPuncakKonsumsi, it, startDate = "2007-12-20", endDate = "2007-12-20")

                    } else{
                        Log.e("fetch Peak", "null")
                    }
                })
            }
        })


        //Puncak Intensitas
        userViewModel.userId.observe(viewLifecycleOwner, Observer {userId->
            if (userId!=null){
                homeViewModel.callApiGetPowerUsage(userId = userId, startDate = "2007-12-20", endDate = "2007-12-20")
                homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer {
                    if (it!=null){
                        Log.e("fetch Peak", "success")
                        Log.e("data Peak", "${it.size}")
                        val barChartPuncakIntensitas = binding.chartPuncakIntesitas
                        showPeakGlobalIntensityTrend(barChartPuncakIntensitas, it, startDate = "2007-12-20", endDate = "2007-12-20")

                    } else{
                        Log.e("fetch Peak", "null")
                    }
                })
            }
        })

    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDailyConsumptionTrend(lineChart: LineChart, dataList: List<Data>, startDate: String, endDate: String) {
        val dailyAverages = calculateDailyAverages(dataList, startDate, endDate)
        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

//        for (i in dailyAverages.indices) {
//            entries.add(Entry(i.toFloat(), dailyAverages[i]))
//            labels.add("Day ${i + 1}") // Menambah label hari
//        }
        for ((index, dailyAverage) in dailyAverages.withIndex()) {
            entries.add(Entry(index.toFloat(), dailyAverage))
            labels.add("Day ${index}") // Add a label for each data point
        }

        val dataSet = LineDataSet(entries, "Daily Consumption")
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        val description = Description()
        description.text = "Daily Consumption Trend"
        lineChart.description = description

        // Pengaturan sumbu X
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Pengaturan sumbu Y
        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f

        lineChart.invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDailyAverages(dataList: List<Data>, startDate: String, endDate: String): List<Float> {
        val dailyAverages = mutableListOf<Float>()

        // Inisialisasi tanggal pertama dan terakhir
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        // Inisialisasi total konsumsi harian dan jumlah sampel harian
        var dailyConsumptionTotal = 0f
        var dailySampleCount = 0

        // Iterasi setiap hari dalam rentang tanggal
        var currentDate = start
        while (!currentDate.isAfter(end)) {
            // Iterasi setiap data dan akumulasikan konsumsi harian
            for (data in dataList) {
                val dataDate = LocalDate.parse(data.date)
                if (dataDate == currentDate) {
                    dailyConsumptionTotal += data.globalActivePower.toFloat()
                    dailySampleCount++
                }
            }
            // Hitung rata-rata konsumsi harian jika ada sampel
            val dailyAverage = if (dailySampleCount > 0) {
                dailyConsumptionTotal / dailySampleCount
            } else {
                0f
            }
            // Tambahkan rata-rata konsumsi harian ke dalam list
            dailyAverages.add(dailyAverage)
            // Reset total konsumsi harian dan jumlah sampel harian untuk hari berikutnya
            dailyConsumptionTotal = 0f
            dailySampleCount = 0
            // Tambahkan satu hari ke tanggal saat ini
            currentDate = currentDate.plusDays(1)
        }

        return dailyAverages
    }private fun calculatePeakConsumption(dataList: List<Data>, startDate: String, endDate: String): Map<Int, Float> {
        val peakConsumptions = mutableMapOf<Int, Float>()
        val hourDataCount = mutableMapOf<Int, Int>()

        // Iterate through the data to sum up consumption for each hour within the specified date range
        for (data in dataList) {
            val dataDate = data.date
            val dataHour = data.time.split(":")[0].toInt() // Extract hour from time

            // Check if the data is within the specified date range
            if (dataDate >= startDate && dataDate <= endDate) {
                val consumption = data.globalActivePower.toFloat()

                // Update total consumption and data count for the hour
                if (!peakConsumptions.containsKey(dataHour)) {
                    peakConsumptions[dataHour] = consumption
                    hourDataCount[dataHour] = 1
                } else {
                    peakConsumptions[dataHour] = peakConsumptions[dataHour]!! + consumption
                    hourDataCount[dataHour] = hourDataCount[dataHour]!! + 1
                }
            }
        }

        // Calculate average consumption for each hour
        for ((hour, totalConsumption) in peakConsumptions) {
            val dataCount = hourDataCount[hour] ?: 0
            if (dataCount > 0) {
                peakConsumptions[hour] = totalConsumption / dataCount
            }
        }

        return peakConsumptions
    }

    private fun showPeakConsumptionTrend(barChart: BarChart, dataList: List<Data>, startDate: String, endDate: String) {
        // Calculate peak consumption for each hour
        val peakConsumptions = calculatePeakConsumption(dataList, startDate, endDate)

        // Prepare entries for the chart
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        for ((hour, consumption) in peakConsumptions) {
            entries.add(BarEntry(hour.toFloat(), consumption))
            labels.add("$hour:00") // Add a label for each hour
        }

        // Create a dataset and add entries to it
        val dataSet = BarDataSet(entries, "Peak Consumption")
        val barData = BarData(dataSet)
        barChart.data = barData

        // Set description for the chart
        val description = Description()
        description.text = "Peak Consumption Trend"
        barChart.description = description

        // Customize X axis
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Customize Y axis
        val yAxis = barChart.axisLeft
        yAxis.axisMinimum = 0f

        // Invalidate and refresh the chart
        barChart.invalidate()
    }



    // Function to calculate maximum global intensity for each hour
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateMaxGlobalIntensity(dataList: List<Data>, startDate: String, endDate: String): Map<Int, Float> {
        val maxGlobalIntensity = mutableMapOf<Int, Float>()
        val hourDataCount = mutableMapOf<Int, Int>()

        // Iterate through the data to sum up global intensity for each hour within the specified date range
        for (data in dataList) {
            val dataDate = LocalDate.parse(data.date)
            val dataHour = data.time.split(":")[0].toInt() // Extract hour from time

            // Check if the data is within the specified date range
            if (dataDate >= LocalDate.parse(startDate) && dataDate <= LocalDate.parse(endDate)) {
                val intensity = data.globalIntensity

                // Update total global intensity and data count for the hour
                if (!maxGlobalIntensity.containsKey(dataHour)) {
                    maxGlobalIntensity[dataHour] = intensity.toFloat()
                    hourDataCount[dataHour] = 1
                } else {
                    if (intensity > maxGlobalIntensity[dataHour]!!) {
                        maxGlobalIntensity[dataHour] = intensity.toFloat()
                    }
                    hourDataCount[dataHour] = hourDataCount[dataHour]!! + 1
                }
            }
        }

        return maxGlobalIntensity
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPeakGlobalIntensityTrend(barChart: BarChart, dataList: List<Data>, startDate: String, endDate: String) {
        // Calculate maximum global intensity for each hour
        val maxGlobalIntensity = calculateMaxGlobalIntensity(dataList, startDate, endDate)

        // Prepare entries for the chart
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        for ((hour, intensity) in maxGlobalIntensity) {
            entries.add(BarEntry(hour.toFloat(), intensity))
            labels.add("$hour:00") // Add a label for each hour
        }

        // Create a dataset and add entries to it
        val dataSet = BarDataSet(entries, "Peak Global Intensity")
        val barData = BarData(dataSet)
        barChart.data = barData

        // Set description for the chart
        val description = Description()
        description.text = "Peak Global Intensity Trend"
        barChart.description = description

        // Customize X axis
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Customize Y axis
        val yAxis = barChart.axisLeft
        yAxis.axisMinimum = 0f

        // Invalidate and refresh the chart
        barChart.invalidate()
    }









}
