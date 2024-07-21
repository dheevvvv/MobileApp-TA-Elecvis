package com.dheevvvv.taelecvis.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.database_room.AlertsData
import com.dheevvvv.taelecvis.databinding.FragmentNotificationBinding
import com.dheevvvv.taelecvis.view.adapter.NotifAlertsAdapter
import com.dheevvvv.taelecvis.view.helper.NotificationHelper
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel
import com.dheevvvv.taelecvis.viewmodel.NotificationAlertsViewModel
import com.dheevvvv.taelecvis.viewmodel.UserViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val notificationAlertsViewModel: NotificationAlertsViewModel by activityViewModels()
    private val userViewModel:UserViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var selectedDate: String = ""
    private var userId: Int = 0
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 123


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showListNotifAlerts()
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_notificationFragment_to_homeFragment)
                    true
                }

                R.id.energy -> {
                    findNavController().navigate(R.id.action_notificationFragment_to_consumptionTransactionFragment)
                    true
                }

                R.id.report -> {
                    findNavController().navigate(R.id.action_notificationFragment_to_reportFragment)
                    true
                }

                R.id.account -> {
                    findNavController().navigate(R.id.action_notificationFragment_to_profileFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }

        binding.ivCalAlert.setOnClickListener {
            showDatePickerDialog()
        }
        binding.tvDateAlert.setOnClickListener {
            showDatePickerDialog()
        }

        checkNotifPermission()

        userViewModel.getUserId()
        userViewModel.userId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            userId = it
            showListNotifAlerts()
        })


        binding.btnCreateAlert.setOnClickListener {
            val kwhLimit = binding.etKwhLimitAlerts.text.toString()
            val date = selectedDate
            if (kwhLimit.isEmpty() || selectedDate.isEmpty()){
                Toast.makeText(context, "Mohon Masukan kWh dan Memilih Tanggal", Toast.LENGTH_SHORT).show()
            } else {
                val alert = AlertsData(0, kwh = kwhLimit, userId = userId, date = date, statusActive = true, "")
                notificationAlertsViewModel.insertNotifAlerts(alert)
                Toast.makeText(requireContext(), "Alert berhasil dibuat", Toast.LENGTH_SHORT).show()
                showListNotifAlerts()
                Log.d("NOTIF_DEBUG", "Adding alert: $alert")
                val title = "Alert Berhasil Dibuat"
                val message = "Kamu akan diingatkan saat kWh mencapai batas $kwhLimit kWh pada $date"
                val notificationHelper = NotificationHelper(requireContext())
                notificationHelper.createNotificationChannel()
                notificationHelper.showNotification(requireContext() ,title, message)
            }
        }
        

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, lanjutkan dengan menampilkan notifikasi
                Toast.makeText(requireContext(), "Izin notifikasi diberikan", Toast.LENGTH_SHORT).show()
            } else {
                // Izin tidak diberikan, tampilkan pesan atau tindakan lain sesuai kebutuhan aplikasi Anda
                Toast.makeText(requireContext(), "Izin diperlukan untuk menampilkan notifikasi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotifPermission(){
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Izin diberikan, lanjutkan dengan menampilkan notifikasi
            Toast.makeText(requireContext(), "Izin notifikasi telah diberikan", Toast.LENGTH_SHORT).show()
        } else {
            // Izin belum diberikan, minta izin kepada pengguna
            requestNotificationPermission()
        }
    }

    private fun getKwh(dateString: String, callback: (Double) -> Unit) {
        homeViewModel.callApiGetPowerUsage(userId, dateString, dateString)
        homeViewModel.powerUsageData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val totalKwh = it.sumOf { it.globalActivePower }
            Log.d("NOTIF_DEBUG", "Current KWH from callback: $totalKwh")
            callback(totalKwh)
        })
    }

//    private fun getKwh(dateString: String, callback: (Double) -> Unit) {
//        val totalKwh = 150.0 // Contoh nilai KWH manual
//        Log.d("NOTIF_DEBUG", "Current KWH from callback: $totalKwh")
//        callback(totalKwh)
//    }




    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    fun showListNotifAlerts(){
        notificationAlertsViewModel.getNotifAlerts(userId)
        notificationAlertsViewModel.notifAlerts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it!=null){
                val adapter = NotifAlertsAdapter(requireContext(),it)
                binding.rvAlerts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.rvAlerts.adapter = adapter

                adapter.onClick = {
                    val alertDialogBuilder = AlertDialog.Builder(requireContext())
                    alertDialogBuilder.setTitle("Konfirmasi Delete")
                    alertDialogBuilder.setMessage("Apakah Anda yakin ingin menghapus?")
                    alertDialogBuilder.setPositiveButton("Ya") { _, _ ->
                        notificationAlertsViewModel.deleteNotifAlerts(it.alertsId, it.userId)
                        Toast.makeText(requireContext(), "Alert berhasil dihapus", Toast.LENGTH_SHORT).show()
                        showListNotifAlerts()
                    }
                    alertDialogBuilder.setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }

                // Periksa dan tampilkan notifikasi berdasarkan alert
                val today: Date = Calendar.getInstance().time
                for (alert in it) {
                    val alertDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(alert.date)
                    if (alertDate != null && isSameDay(alertDate, today)) {
                        getKwh(alert.date) { currentKwh ->
                            if (currentKwh >= alert.kwh.toDouble() && !isAlertExpired(alertDate)) {
                                Log.d("NOTIF_DEBUG", "Triggering alert with limit: ${alert.kwh} KWH")
                                val title = "Peringatan KWH"
                                val message = "Nilai KWH saat ini ($currentKwh) telah mencapai batas yang ditetapkan (${alert.kwh})."
                                val notificationHelper = NotificationHelper(requireContext())
                                notificationHelper.createNotificationChannel()
                                notificationHelper.showNotification(requireContext(), title, message)

                                // Simpan waktu terakhir terpicu saat notifikasi terpicu
                                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                                alert.lastTriggeredTime = currentTime
                                notificationAlertsViewModel.updateNotifAlerts(alert)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Alerts data null", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun isAlertExpired(alertDate: Date): Boolean {
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DAY_OF_YEAR, -1) // Mengurangi satu hari dari tanggal sekarang
        val expired = alertDate.before(currentDate.time)
        Log.d("NOTIF_DEBUG", "Current Date: $currentDate, Alert Date: $alertDate, Is Expired: $expired")
        return expired // Mengembalikan true jika tanggal alert telah lewat
    }

    fun isSameDay(date1: Date?, date2: Date): Boolean {
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        if (date1 != null) {
            calendar1.time = date1
        }
        calendar2.time = date2
        val sameDay = calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
        Log.d("NOTIF_DEBUG", "Date1: $date1, Date2: $date2, Is Same Day: $sameDay")
        return sameDay
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view: DatePicker, selectedYear: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedDate = "$selectedYear-${monthOfYear + 1}-$dayOfMonth"
                binding.tvDateAlert.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }



}