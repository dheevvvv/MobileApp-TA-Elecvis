package com.dheevvvv.taelecvis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dheevvvv.taelecvis.database_room.AlertsDAO
import com.dheevvvv.taelecvis.database_room.AlertsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationAlertsViewModel @Inject constructor(val alertsDAO: AlertsDAO): ViewModel() {
    private var _notifAlerts : MutableLiveData<List<AlertsData>> = MutableLiveData()
    val notifAlerts : LiveData<List<AlertsData>> get() = _notifAlerts

    var _statusActive = MutableLiveData<Boolean>()
    val statusActive: LiveData<Boolean> get() = _statusActive

    val statusActiveMap: MutableMap<Int, Boolean> = mutableMapOf()

    private var _listActiveAlerts : MutableLiveData<List<AlertsData>> = MutableLiveData()
    val listActiveAlerts : LiveData<List<AlertsData>> get() = _listActiveAlerts


    fun loadStatusActive(alertId: Int, userId: Int){
        viewModelScope.launch {
            val statusActive = alertsDAO.getStatusActive(alertId, userId)
            statusActiveMap[alertId] = statusActive
        }
    }

    fun insertNotifAlerts(alertsData: AlertsData){
        viewModelScope.async {
            alertsDAO.insert(alertsData)
        }
    }

    fun updateNotifAlerts(alertsData: AlertsData){
        viewModelScope.async {
            alertsDAO.update(alertsData)
        }
    }

    fun deleteNotifAlerts(alertsId: Int, userId: Int){
        viewModelScope.async {
            alertsDAO.deleteAlertsByIdAndUser(alertsId, userId)
        }
    }

    fun getNotifAlerts(userId: Int){
        viewModelScope.launch {
            val listAlerts = alertsDAO.getAlertsByUser(userId)
            _notifAlerts.postValue(listAlerts)
        }
    }

    fun getActiveAlerts(userId: Int){
        viewModelScope.launch {
            val listActiveAlerts = alertsDAO.getActiveAlerts(userId)
            _listActiveAlerts.postValue(listActiveAlerts)
        }
    }
}