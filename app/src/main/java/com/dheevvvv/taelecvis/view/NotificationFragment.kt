package com.dheevvvv.taelecvis.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.database_room.AlertsData
import com.dheevvvv.taelecvis.databinding.FragmentNotificationBinding
import com.dheevvvv.taelecvis.view.adapter.NotifAlertsAdapter
import com.dheevvvv.taelecvis.viewmodel.NotificationAlertsViewModel
import com.dheevvvv.taelecvis.viewmodel.UserViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*


class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val notificationAlertsViewModel: NotificationAlertsViewModel by activityViewModels()
    private val userViewModel:UserViewModel by activityViewModels()
    private var selectedDate: String = ""
    private var userId: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        userViewModel.getUserId()
        userViewModel.userId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            userId = it
            binding.btnCreateAlert.setOnClickListener {
                val kwhLimit = binding.etKwhLimitAlerts.text.toString()
                val date = selectedDate
                if (kwhLimit.isEmpty() || selectedDate.isEmpty()){
                    Toast.makeText(context, "Mohon Masukan kWh dan Memilih Tanggal", Toast.LENGTH_SHORT).show()
                } else {
                    notificationAlertsViewModel.insertNotifAlerts(AlertsData(0, kwh = kwhLimit, userId = userId, date = date, statusActive = true))
                }
            }
        })

        notificationAlertsViewModel.getNotifAlerts(userId)
        notificationAlertsViewModel.notifAlerts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it!=null){
                val adapter = NotifAlertsAdapter(it)
                binding.rvAlerts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.rvAlerts.adapter = adapter
                adapter.onClick = {
                    notificationAlertsViewModel.deleteNotifAlerts(alertsId = it.alertsId, userId = userId)
                }
            } else{
                Toast.makeText(context, "alerts data null", Toast.LENGTH_SHORT).show()
            }
        })



    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view: DatePicker, selectedYear: Int, monthOfYear: Int, dayOfMonth: Int ->
                selectedDate = "$dayOfMonth/${monthOfYear + 1}/$selectedYear"
                binding.tvDateAlert.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }


}