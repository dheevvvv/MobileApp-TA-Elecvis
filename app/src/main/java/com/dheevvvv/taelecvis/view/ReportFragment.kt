package com.dheevvvv.taelecvis.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.red
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.Manifest
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentReportBinding
import com.dheevvvv.taelecvis.model.power_usage.Data
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel
import com.dheevvvv.taelecvis.viewmodel.ReportViewModel
import com.dheevvvv.taelecvis.viewmodel.UserViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private var selectedStartDate:String = ""
    private var selectedEndDate:String = ""
    private var pengeluaranKwhBulanIni: Float = 0F
    private var pengeluaranRpBulanIni: Float = 0F
    private var pengeluaranKwhBulanLalu: Float = 0F
    private var pengeluaranRpBulanLalu: Float = 0F
    private val userViewModel: UserViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val REQUEST_CODE_PERMISSION = 101
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_reportFragment_to_homeFragment)
                    true
                }

                R.id.energy -> {
                    findNavController().navigate(R.id.action_reportFragment_to_consumptionTransactionFragment)
                    true
                }

                R.id.notification -> {
                    findNavController().navigate(R.id.action_reportFragment_to_notificationFragment)
                    true
                }

                R.id.account -> {
                    findNavController().navigate(R.id.action_reportFragment_to_profileFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }

        binding.btnPilihTanggal.setOnClickListener {
            showDateRangePicker()
        }

        reportViewModel.selectedStartDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer { startDate->
            if (startDate!=null){
                selectedStartDate  = startDate
                reportViewModel.selectedEndDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer { endDate->
                    selectedEndDate = endDate
                    bulanIni()
                    bulanlalu()
                    selisih(pengeluaranKwhBulanIni, pengeluaranKwhBulanLalu, pengeluaranRpBulanIni, pengeluaranRpBulanLalu)
                })
            } else{
                Toast.makeText(context, "Pilih Tanggal terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        })



        binding.cvBulanIni.setOnClickListener {
            binding.tvPengeluaranKwh.setText("$pengeluaranKwhBulanIni kWh")
            binding.tvPengeluaranRupiah.setText("Rp. $pengeluaranRpBulanIni")
        }

        binding.cvBulanLalu.setOnClickListener {
            binding.tvPengeluaranKwh.setText("$pengeluaranKwhBulanLalu kWh")
            binding.tvPengeluaranRupiah.setText("Rp. $pengeluaranRpBulanLalu")
        }



    }

    @SuppressLint("SetTextI18n")
    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()

        val constraintsBuilder = CalendarConstraints.Builder()
        val calendar = Calendar.getInstance()
        val minDate = calendar.timeInMillis
        calendar.add(Calendar.YEAR, 1)
        val maxDate = calendar.timeInMillis
        constraintsBuilder.setStart(minDate)
        constraintsBuilder.setEnd(maxDate)
        builder.setCalendarConstraints(constraintsBuilder.build())

        val picker = builder.build()

        picker.addOnPositiveButtonClickListener { selection ->
            val startDate = Date(selection.first ?: 0)
            val endDate = Date(selection.second ?: 0)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDateString = dateFormat.format(startDate)
            val endDateString = dateFormat.format(endDate)


            // Simpan startDate dan endDate terpilih
            reportViewModel.saveSelectedStartDate(startDateString)
            reportViewModel.saveSelectedEndDate(endDateString)

            binding.tvSelectedDate.setText("$startDateString - $endDateString")
        }

        picker.show(childFragmentManager, picker.toString())
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculatePengeluaranConsumption(dataList: List<Data>, startDate: String, endDate: String): Float {

        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        var consumptionTotal = 0f

        // Iterasi setiap hari dalam rentang tanggal
        var currentDate = start
        while (!currentDate.isAfter(end)) {
            // Iterasi setiap data dan akumulasikan konsumsi harian
            for (data in dataList) {
                val dataDate = LocalDate.parse(data.date)
                if (dataDate == currentDate) {
                    consumptionTotal += data.globalActivePower.toFloat()
                }
            }
        }
        val consumptionPrice = consumptionTotal * 1500
        binding.tvPengeluaranKwh.setText("$consumptionTotal kWh")
        binding.tvPengeluaranRupiah.setText("Rp $consumptionPrice")

        return consumptionTotal
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bulanIni(){
        userViewModel.userId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val userId = it
            homeViewModel.callApiGetPowerUsage(userId, selectedStartDate, selectedEndDate)
            homeViewModel.powerUsageData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { data->
                if (data!=null){
                    calculatePengeluaranConsumption(data, selectedStartDate, selectedEndDate)
                    val pengeluaran = calculatePengeluaranConsumption(data, selectedStartDate, selectedEndDate)
                    pengeluaranKwhBulanIni = pengeluaran
                    val pengeluaranRupiahBulanIni = pengeluaranKwhBulanIni * 1500
                    pengeluaranRpBulanIni = pengeluaranRupiahBulanIni
                }
            })
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bulanlalu(){
        userViewModel.userId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val userId = it
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            // Convert selectedStartDate to Date object
            val startDate = dateFormat.parse(selectedStartDate)

            // Set the calendar to the startDate
            calendar.time = startDate!!

            // Set the calendar to the start of the current month
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            // Subtract 1 day to get the end of the previous month
            calendar.add(Calendar.DAY_OF_MONTH, -1)

            // Set the endDate for last month to the last day of the previous month
            val endDateForLastMonth = dateFormat.format(calendar.time)

            // Set the startDate for last month to 20 days before the end of last month
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 30)
            val startDateForLastMonth = dateFormat.format(calendar.time)

            homeViewModel.callApiGetPowerUsage(userId, startDateForLastMonth, endDateForLastMonth)
            homeViewModel.powerUsageData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {data->
                if (data!=null){
                    calculatePengeluaranConsumption(data, startDateForLastMonth, endDateForLastMonth)
                    val pengeluaran = calculatePengeluaranConsumption(data, startDateForLastMonth, endDateForLastMonth)
                    pengeluaranKwhBulanLalu = pengeluaran
                    val pengeluaranRupiahBulanLalu = pengeluaranKwhBulanLalu * 1500
                    pengeluaranRpBulanLalu = pengeluaranRupiahBulanLalu
                }
            })

        })
    }

    @SuppressLint("SetTextI18n")
    private fun selisih(pengeluaranKwhBulanIni:Float, pengeluaranKwhBulanLalu:Float, rpBulanIni:Float, rpBulanLalu:Float){
        val selisihKwh = pengeluaranKwhBulanIni - pengeluaranKwhBulanLalu
        val status = if (selisihKwh > 0 ) {
            "boros"
        } else if (selisihKwh < 0 ) {
            "hemat"
        } else {
            "unknown"
        }

        val selisihRp = rpBulanIni - rpBulanLalu

        val percentageSelisihKwh = (selisihKwh / pengeluaranKwhBulanLalu) * 100

        binding.tvSelisihKwh.setText("$selisihKwh kWh")
        binding.tvSelisihRp.setText("Rp. $selisihRp")

        if (status=="boros"){
            binding.tvHematBoros.setText("Boros")
            binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(), R.color.red)
        } else if (status=="hemat"){
            binding.tvHematBoros.setText("Hemat")
            binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(), R.color.green)
        }

        binding.progressText.setText("$percentageSelisihKwh %")

    }

}