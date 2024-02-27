package com.dheevvvv.taelecvis.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
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
        userViewModel.userId.observe(viewLifecycleOwner, Observer {userId->
            if (userId!=null){
                homeViewModel.callApiGetPowerUsage(userId = userId, startDate = "2006-12-10", endDate = "2006-12-17")
                homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer {
                    if (it!=null){
                        Log.e("fetch", "success")
                        Log.e("data", "${it.size}")
                        val lineChart = binding.lineChart
                        showDailyConsumptionTrend(lineChart, it)

                    } else{
                        Log.e("fetch", "null")
                    }
                })
            }
        })

    }



    private fun showDailyConsumptionTrend(lineChart: LineChart,dataList: List<Data>) {
        val dailyAverages = calculateDailyAverages(dataList)
        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

//        for (i in dailyAverages.indices) {
//            entries.add(Entry(i.toFloat(), dailyAverages[i]))
//            labels.add("Day ${i + 1}") // Menambah label hari
//        }
        for ((index, dailyAverage) in dailyAverages.withIndex()) {
            entries.add(Entry(index.toFloat(), dailyAverage))
            labels.add("Day $index") // Add a label for each data point
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

    private fun calculateDailyAverages(dataList: List<Data>): List<Float> {
        val dailyAverages = mutableListOf<Float>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        var currentDate = ""
        var totalConsumption = 0f
        var count = 0

        for (data in dataList) {
            val date = data.date
            if (date != currentDate) {
                // Hitung rata-rata untuk hari sebelumnya
                if (count != 0) {
                    dailyAverages.add(totalConsumption / count)
                }

                // Reset variabel untuk hari baru
                currentDate = date
                totalConsumption = 0f
                count = 0
            }

            totalConsumption += data.globalActivePower.toFloat()
            count++
        }

        // Tambahkan rata-rata untuk hari terakhir
        if (count != 0) {
            dailyAverages.add(totalConsumption / count)
        }

        return dailyAverages
    }













}
