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
import androidx.fragment.app.activityViewModels
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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

private const val LOW_VOLTAGE_THRESHOLD = 198
private const val HIGH_VOLTAGE_THRESHOLD = 242 // batas tinggi tegangan
@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()


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
                    true
                }

                R.id.energy -> {
                    findNavController().navigate(R.id.action_homeFragment_to_consumptionTransactionFragment)
                    true
                }

                R.id.report -> {
                    findNavController().navigate(R.id.action_homeFragment_to_reportFragment)
                    true

                }

                R.id.notification -> {
                    findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
                    true

                }

                else -> false
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
//                        Log.e("fetch Peak", "success")
//                        Log.e("data Peak", "${it.size}")
                        val barChartPuncakIntensitas = binding.chartPuncakIntesitas
                        showPeakGlobalIntensityTrend(barChartPuncakIntensitas, it, startDate = "2007-12-20", endDate = "2007-12-20")

                    } else{
                        Log.e("fetch Peak", "null")
                    }
                })
            }
        })


        //Stabilitas Tegangan Voltage
        userViewModel.userId.observe(viewLifecycleOwner, Observer {userId->
            if (userId!=null){
                homeViewModel.callApiGetPowerUsage(userId = userId, startDate = "2007-12-20", endDate = "2007-12-20")
                homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer {
                    if (it!=null){
//                        Log.e("fetch Peak", "success")
//                        Log.e("data Peak", "${it.size}")
                        val barChartStabilitasTegangan = binding.chartVoltageBar
                        showVoltageStabilityBarChart(barChartStabilitasTegangan, it, startDate = "2007-12-20", endDate = "2007-12-20")

                    } else{
                        Log.e("fetch Peak", "null")
                    }
                })
            }
        })

        //sub metering
        userViewModel.userId.observe(viewLifecycleOwner, Observer {userId->
            if (userId!=null){
                homeViewModel.callApiGetPowerUsage(userId = userId, startDate = "2007-12-20", endDate = "2007-12-20")
                homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer {
                    if (it!=null){
//                        Log.e("fetch Peak", "success")
//                        Log.e("data Peak", "${it.size}")
                        val pieChartSubMetering = binding.pieCharrSubMetering
                        showSubMeteringCompositionPieChart(pieChartSubMetering, it, startDate = "2007-12-20", endDate = "2007-12-20")

                    } else{
                        Log.e("fetch Peak", "null")
                    }
                })
            }
        })

        binding.btnTrenKonsumsiHarian.setOnClickListener {
            val idChartData = 1
            val bundle = Bundle()
            bundle.putInt("dataChartId", idChartData)
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }
        binding.btnPuncakKonsumsiEnergi.setOnClickListener {
            val idChartData = 2
            val bundle = Bundle()
            bundle.putInt("dataChartId", idChartData)
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }
        binding.btnVoltage.setOnClickListener {
            val idChartData = 3
            val bundle = Bundle()
            bundle.putInt("dataChartId", idChartData)
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }
        binding.btnPuncakIntensitas.setOnClickListener {
            val idChartData = 4
            val bundle = Bundle()
            bundle.putInt("dataChartId", idChartData)
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }
        binding.btnSubMetering.setOnClickListener {
            val idChartData = 5
            val bundle = Bundle()
            bundle.putInt("dataChartId", idChartData)
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }

    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun showDailyConsumptionTrend(lineChart: LineChart, dataList: List<Data>, startDate: String, endDate: String) {
        val dailyAverages = calculateDailyAverages(dataList, startDate, endDate)
        val totalConsumption = calculateTotalConsumption(dataList, startDate, endDate)
        homeViewModel.saveTotalConsumptionEnergyTren(totalConsumption)
        val entries = ArrayList<Entry>()
        val labels = ArrayList<String>()

        for ((index, dailyAverage) in dailyAverages.withIndex()) {
            entries.add(Entry(index.toFloat(), dailyAverage))
            labels.add("Day ${index}") // Add a label for each data point
        }

        val dataSet = LineDataSet(entries, "Daily Consumption (kW)")
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        homeViewModel.updateChartDataTrenKonsumsiHarian(lineData)
        homeViewModel.saveLabelsTrenKonsumsiHarian(labels)

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
    fun calculateTotalConsumption(dataList: List<Data>, startDate: String, endDate: String): Float {
        var totalConsumption = 0f

        // Inisialisasi tanggal pertama dan terakhir
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        // Iterasi setiap data dalam rentang tanggal dan akumulasikan total konsumsi
        for (data in dataList) {
            val dataDate = LocalDate.parse(data.date)
            if (!dataDate.isBefore(start) && !dataDate.isAfter(end)) {
                totalConsumption += data.globalActivePower.toFloat()
            }
        }

        return totalConsumption
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateDailyAverages(dataList: List<Data>, startDate: String, endDate: String): List<Float> {
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
    }

    fun calculatePeakConsumption(dataList: List<Data>, startDate: String, endDate: String): Map<Int, Float> {
        val peakConsumptions = mutableMapOf<Int, Float>()
        val hourDataCount = mutableMapOf<Int, Int>()

        // Iterate through the data to find the peak consumption for each hour within the specified date range
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
                    val currentPeak = peakConsumptions[dataHour]!!
                    if (consumption > currentPeak) {
                        peakConsumptions[dataHour] = consumption
                    }
                    hourDataCount[dataHour] = hourDataCount[dataHour]!! + 1
                }
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
        val dataSet = BarDataSet(entries, "Peak Consumption (kW)")
        val barData = BarData(dataSet)
        barChart.data = barData
        homeViewModel.updateChartDataPeakKonsumsi(barData)
        homeViewModel.saveLabelsPeakKonsumsi(labels)

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
    fun calculateMaxGlobalIntensity(dataList: List<Data>, startDate: String, endDate: String): Map<Int, Float> {
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
        val dataSet = BarDataSet(entries, "Peak Global Intensity (ampere)")
        val barData = BarData(dataSet)
        barChart.data = barData

        // Set description for the chart
        val description = Description()
        description.text = "Peak Global Intensity Trend"
        barChart.description = description
        homeViewModel.updateChartDataIntensitas(barData)
        homeViewModel.saveLabelsIntensitas(labels)


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

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAverageVoltage(dataList: List<Data>, startDate: String, endDate: String): Map<Int, Float> {
        val averageVoltages = mutableMapOf<Int, Float>()
        val hourDataCount = mutableMapOf<Int, Int>()

        // Iterate through the data to sum up voltage for each hour within the specified date range
        for (data in dataList) {
            val dataDate = LocalDate.parse(data.date)
            val dataHour = data.time.split(":")[0].toInt() // Extract hour from time

            // Check if the data is within the specified date range
            if (dataDate >= LocalDate.parse(startDate) && dataDate <= LocalDate.parse(endDate)) {
                val voltage = data.voltage

                // Update total voltage and data count for the hour
                if (!averageVoltages.containsKey(dataHour)) {
                    averageVoltages[dataHour] = voltage.toFloat()
                    hourDataCount[dataHour] = 1
                } else {
                    averageVoltages[dataHour] = averageVoltages[dataHour]!! + voltage.toFloat()
                    hourDataCount[dataHour] = hourDataCount[dataHour]!! + 1
                }
            }
        }

        // Calculate average voltage for each hour
        for ((hour, totalVoltage) in averageVoltages) {
            val dataCount = hourDataCount[hour] ?: 0
            if (dataCount > 0) {
                averageVoltages[hour] = totalVoltage / dataCount
            }
        }

        return averageVoltages
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun showVoltageStabilityBarChart(barChart: BarChart, dataList: List<Data>, startDate: String, endDate: String) {
        // Calculate average voltage for each hour
        val averageVoltages = calculateAverageVoltage(dataList, startDate, endDate)

        // Prepare entries for the chart
        val entries = ArrayList<BarEntry>()
        val disturbedEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        for ((hour, voltage) in averageVoltages) {
            entries.add(BarEntry(hour.toFloat(), voltage))
            labels.add("$hour:00") // Add a label for each hour

            // Check for disturbed points
            if (voltage < LOW_VOLTAGE_THRESHOLD || voltage > HIGH_VOLTAGE_THRESHOLD) {
                disturbedEntries.add(BarEntry(hour.toFloat(), voltage))
            }
        }

        // Create dataset for average voltages
        val dataSet = BarDataSet(entries, "Normal Voltage")
        dataSet.color = Color.BLUE // Set color for the bars

        // Create dataset for disturbed points
        val disturbedDataSet = BarDataSet(disturbedEntries, "Disturbed Voltage")
        disturbedDataSet.color = Color.RED // Set color for disturbed bars

        // Create bar data and add datasets to it
        val barData = BarData(dataSet, disturbedDataSet)
        barChart.data = barData
        homeViewModel.updateChartDataVoltage(barData)
        homeViewModel.saveLabelsVoltage(labels)

        // Set description for the chart
        val description = Description()
        description.text = "Voltage Stability Bar Chart"
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateSubMeteringComposition(dataList: List<Data>, startDate: String, endDate: String): Map<String, Float> {
        val subMeteringComposition = mutableMapOf<String, Float>()

        // Filter data for the specified date range
        val filteredData = dataList.filter { it.date in startDate..endDate }

        // Calculate total energy consumption for the specified date range
        val totalEnergy = filteredData.sumOf { it.subMetering1.toDouble() + it.subMetering2.toDouble() + it.subMetering3.toDouble() }
            .toFloat()
        val totalKwh = totalEnergy / 1000
        homeViewModel.saveTotalSubmeter(totalKwh)

        // Calculate composition of sub-metering
        val subMetering1Energy = filteredData.sumOf { it.subMetering1.toDouble() }.toFloat()
        val subMetering2Energy = filteredData.sumOf { it.subMetering2.toDouble() }.toFloat()
        val subMetering3Energy = filteredData.sumOf { it.subMetering3.toDouble() }.toFloat()

        // Calculate percentage composition
        subMeteringComposition["Sub-Metering 1"] = (subMetering1Energy / totalEnergy) * 100
        subMeteringComposition["Sub-Metering 2"] = (subMetering2Energy / totalEnergy) * 100
        subMeteringComposition["Sub-Metering 3"] = (subMetering3Energy / totalEnergy) * 100

        return subMeteringComposition
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showSubMeteringCompositionPieChart(pieChart: PieChart, dataList: List<Data>, startDate: String, endDate: String) {
        // Calculate sub-metering composition
        val subMeteringComposition = calculateSubMeteringComposition(dataList, startDate, endDate)

        // Prepare entries for the pie chart
        val entries = ArrayList<PieEntry>()
        val labels = ArrayList<String>()

        subMeteringComposition.forEach { (subMeter, percentage) ->
            val descriptionl = getSubMeterDescription(subMeter)
            val label = "$descriptionl"
            entries.add(PieEntry(percentage, label))
            labels.add(descriptionl)
        }

        // Create a dataset and add entries to it
        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        pieChart.setDrawEntryLabels(false)
        dataSet.valueTextSize = 14f


        // Create pie data and set dataset
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))
        homeViewModel.updateChartDataSubmeter(pieData)
        homeViewModel.saveLabelsSubmeter(labels)

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
    }


    private fun getSubMeterDescription(subMeter: String): String {
        return when (subMeter) {
            "Sub-Metering 1" -> "Kitchen"
            "Sub-Metering 2" -> "Laundry Room"
            "Sub-Metering 3" -> " Water-heater and an Air-conditioner."
            else -> ""
        }
    }









}
