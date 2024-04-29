package com.dheevvvv.taelecvis.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentConsumptionTransactionBinding
import com.dheevvvv.taelecvis.model.power_usage.Data
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel
import com.dheevvvv.taelecvis.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ConsumptionTransactionFragment : Fragment() {
    private lateinit var binding: FragmentConsumptionTransactionBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var userId:Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentConsumptionTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_consumptionTransactionFragment_to_homeFragment)
                    true
                }

                R.id.notification -> {
                    findNavController().navigate(R.id.action_consumptionTransactionFragment_to_notificationFragment)
                    true
                }

                R.id.report -> {
                    findNavController().navigate(R.id.action_consumptionTransactionFragment_to_reportFragment)
                    true
                }

                R.id.account -> {
                    findNavController().navigate(R.id.action_consumptionTransactionFragment_to_profileFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }

        show()

        binding.cv1Day.setOnClickListener {
            val startDate = "2007-12-20"
            val endDate = "2007-12-20"
            homeViewModel.callApiGetPowerUsage(userId, startDate, endDate)
            homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer { data->
                if (data!=null){
                    val option = "Hari"
                    val filteredDates = filterDates(startDate, endDate, option)
                    val filteredStartDate = filteredDates.first
                    val filteredEndDate = filteredDates.second
                    calculateSubMetering(data, filteredStartDate, filteredEndDate)

                    binding.cv1Month.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white2))
                    binding.cv1Week.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white2))
                    binding.cv1Day.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color._deep))
                }
            })
        }
        binding.cv1Week.setOnClickListener {
            val startDate = "2007-12-20"
            val endDate = "2007-12-20"
            homeViewModel.callApiGetPowerUsage(userId, startDate, endDate)
            homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer { data->
                if (data!=null){
                    val option = "Minggu"
                    val filteredDates = filterDates(startDate, endDate, option)
                    val filteredStartDate = filteredDates.first
                    val filteredEndDate = filteredDates.second
                    calculateSubMetering(data, filteredStartDate, filteredEndDate)

                    binding.cv1Month.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white2))
                    binding.cv1Week.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color._deep))
                    binding.cv1Day.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white2))
                }
            })
        }
        binding.cv1Month.setOnClickListener {
            val startDate = "2007-12-20"
            val endDate = "2007-12-20"
            homeViewModel.callApiGetPowerUsage(userId, startDate, endDate)
            homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer { data->
                if (data!=null){
                    val option = "Bulan"
                    val filteredDates = filterDates(startDate, endDate, option)
                    val filteredStartDate = filteredDates.first
                    val filteredEndDate = filteredDates.second
                    calculateSubMetering(data, filteredStartDate, filteredEndDate)

                    binding.cv1Month.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color._deep))
                    binding.cv1Week.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white2))
                    binding.cv1Day.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white2))
                }
            })
        }
    }

    private fun show() {
        userViewModel.userId.observe(viewLifecycleOwner, Observer {
            userId = it
            val startDate = "2007-12-20"
            val endDate = "2007-12-20"
            homeViewModel.callApiGetPowerUsage(userId, startDate, endDate)
            homeViewModel.powerUsageData.observe(viewLifecycleOwner, Observer { data->
                if (data!=null){
                    val option = "Per Hari"
                    val filteredDates = filterDates(startDate, endDate, option)
                    val filteredStartDate = filteredDates.first
                    val filteredEndDate = filteredDates.second
                    calculateSubMetering(data, filteredStartDate, filteredEndDate)
                }
            })
        })
    }

    @SuppressLint("SetTextI18n")
    private fun calculateSubMetering(dataList: List<Data>, startDate: String, endDate: String){

        // Filter data for the specified date range
        val filteredData = dataList.filter { it.date in startDate..endDate }

        // Calculate total energy consumption for the specified date range
        val totalEnergy = filteredData.sumOf { it.subMetering1.toDouble() + it.subMetering2.toDouble() + it.subMetering3.toDouble() }
            .toFloat()

        val totalEnergyKwh = totalEnergy / 1000
        val estimatedPrice = totalEnergyKwh * 1500

        // Calculate composition of sub-metering Wh
        val subMetering1Energy = filteredData.sumOf { it.subMetering1.toDouble() }.toFloat()
        val subMetering2Energy = filteredData.sumOf { it.subMetering2.toDouble() }.toFloat()
        val subMetering3Energy = filteredData.sumOf { it.subMetering3.toDouble() }.toFloat()

        // Calculate composition of sub-metering kWh
        val subMetering1EnergykWh = filteredData.sumOf { it.subMetering1.toDouble() / 1000 }.toFloat()
        val subMetering2EnergykWh = filteredData.sumOf { it.subMetering2.toDouble() / 1000 }.toFloat()
        val subMetering3EnergykWh = filteredData.sumOf { it.subMetering3.toDouble() / 1000 }.toFloat()

        binding.tvTotalKwh.setText("$totalEnergyKwh kWh")
        binding.tvTotalPrice.setText("Rp $estimatedPrice")
        binding.tvTotalKwhKitchen.setText("$subMetering1EnergykWh kWh")
        binding.tvTotalKwhLoundryRoom.setText("$subMetering2EnergykWh kWh")
        binding.tvTotalKwhHeater.setText("$subMetering3EnergykWh kWh")
        binding.tvTotalWhKitchen.setText("$subMetering1Energy Wh")
        binding.tvTotalWhLoundryRoom.setText("$subMetering2Energy Wh")
        binding.tvTotalWhHeater.setText("$subMetering3Energy Wh")

    }

    fun filterDates(startDate: String, endDate: String, option: String): Pair<String, String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val filteredStartDate: String
        val filteredEndDate: String

        calendar.time = sdf.parse(startDate)!!

        when (option) {
            "Hari" -> {
                filteredStartDate = startDate
                filteredEndDate = endDate
            }
            "Minggu" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                filteredStartDate = sdf.format(calendar.time)
                filteredEndDate = endDate
            }
            "Bulan" -> {
                calendar.add(Calendar.MONTH, -1)
                filteredStartDate = sdf.format(calendar.time)
                filteredEndDate = endDate
            }
            else -> {
                filteredStartDate = startDate
                filteredEndDate = endDate
            }
        }

        return Pair(filteredStartDate, filteredEndDate)
    }




}