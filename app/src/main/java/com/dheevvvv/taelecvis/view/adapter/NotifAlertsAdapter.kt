package com.dheevvvv.taelecvis.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dheevvvv.taelecvis.database_room.AlertsData
import com.dheevvvv.taelecvis.databinding.ItemAlertsBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class NotifAlertsAdapter(private val alerts: List<AlertsData>) : RecyclerView.Adapter<NotifAlertsAdapter.ViewHolder>() {
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
        holder.binding.tvActiveDisabled.text = if (list.statusActive) "Active" else "Disabled"
        holder.binding.tvTriggered.text = getLastTriggeredText(list.date)
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
                "Never triggered"
            }
        } else {
            "Never triggered"
        }
    }

}