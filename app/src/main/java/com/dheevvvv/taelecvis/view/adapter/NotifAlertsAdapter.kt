package com.dheevvvv.taelecvis.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.database_room.AlertsData
import com.dheevvvv.taelecvis.databinding.ItemAlertsBinding
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotifAlertsAdapter(private val context: Context ,private val alerts: List<AlertsData>) : RecyclerView.Adapter<NotifAlertsAdapter.ViewHolder>() {
    var onClick: ((AlertsData)->Unit)? = null

    class ViewHolder(val binding: ItemAlertsBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = ItemAlertsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = alerts[position]
        holder.binding.tvKwh.text = "${list.kwh} kWh"
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(list.date)
        holder.binding.tvActiveDisabled.text = if (list.statusActive && !isDatePassed(date!!)) {
            holder.binding.tvActiveDisabled.setTextColor(ContextCompat.getColor(context, R.color.green))
            "Active"
        } else {
            holder.binding.tvActiveDisabled.setTextColor(ContextCompat.getColor(context, R.color.red))
            "Disabled"
        }

        holder.binding.tvTriggered.text = getLastTriggeredText(list.lastTriggeredTime)
        holder.binding.tvDate.text = formatDate(list.date)
        holder.binding.ivDelete.setOnClickListener {
            onClick?.invoke(list)
        }

    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    private fun getLastTriggeredText(lastTriggeredTime: String?): String {
        return if (!lastTriggeredTime.isNullOrEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentTime = Date()
            val lastTriggeredDate = sdf.parse(lastTriggeredTime)

            if (lastTriggeredDate != null) {
                val diffInMillis = currentTime.time - lastTriggeredDate.time
                val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
                val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
                val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

                when {
                    diffInMinutes < 60 -> "Last triggered $diffInMinutes minutes ago"
                    diffInHours < 24 -> "Last triggered $diffInHours hours ago"
                    else -> "Last triggered $diffInDays days ago"
                }
            } else {
                "Not yet triggered"
            }
        } else {
            "Not yet triggered"
        }
    }

    fun formatDate(dateString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: return "Invalid Date" // Parsing tanggal

        val calendar = Calendar.getInstance()
        calendar.time = date

        // Mendapatkan hari, nama bulan, dan tahun dari tanggal
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val monthName = DateFormatSymbols().months[month]

        // Menggabungkan komponen untuk membentuk format tanggal yang diinginkan
        return String.format("%02d-%s-%04d", day, monthName, year)
    }

    private fun isDatePassed(date: Date): Boolean {
        val currentDate = Calendar.getInstance().time
        return date.before(currentDate)
    }

}