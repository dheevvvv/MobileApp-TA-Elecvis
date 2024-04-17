package com.dheevvvv.taelecvis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dheevvvv.taelecvis.networking.ApiService
import javax.inject.Inject

class ReportViewModel @Inject constructor(val apiService: ApiService):ViewModel() {

    private val _selectedStartDate = MutableLiveData<String>()
    val selectedStartDate:LiveData<String> get() = _selectedStartDate
    private val _selectedEndDate = MutableLiveData<String>()
    val selectedEndDate:LiveData<String> get() = _selectedEndDate


    fun saveSelectedStartDate(startDate:String){
        _selectedStartDate.postValue(startDate)
    }
    fun saveSelectedEndDate(endDate:String){
        _selectedEndDate.postValue(endDate)
    }

}