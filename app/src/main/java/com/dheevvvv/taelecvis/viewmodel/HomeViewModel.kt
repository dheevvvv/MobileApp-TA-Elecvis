package com.dheevvvv.taelecvis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import com.dheevvvv.taelecvis.model.power_usage.Data
import com.dheevvvv.taelecvis.model.power_usage.GetPowerUsageResponse
import com.dheevvvv.taelecvis.networking.ApiService
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.PieData
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(val userManager: UserManager, val apiService: ApiService): ViewModel() {
    private val _powerUsageData = MutableLiveData<List<Data>>()
    val powerUsageData: LiveData<List<Data>> get() = _powerUsageData

    private val _dataChartTrenKonsumsiHarian = MutableLiveData<LineData>()
    val dataChartTrenKonsumsiHarian: LiveData<LineData> get() = _dataChartTrenKonsumsiHarian
    private val _labelsTrenKonsumsiHarian = MutableLiveData<ArrayList<String>>()
    val labelsTrenKonsumsiHarian:LiveData<ArrayList<String>> get() = _labelsTrenKonsumsiHarian

    private val _dataChartVoltage = MutableLiveData<BarData>()
    val dataChartVoltage: LiveData<BarData> get() = _dataChartVoltage
    private val _labelsVolatge = MutableLiveData<ArrayList<String>>()
    val labelsVoltage:LiveData<ArrayList<String>> get() = _labelsVolatge

    private val _dataChartIntensitas = MutableLiveData<BarData>()
    val dataChartIntensitas: LiveData<BarData> get() = _dataChartIntensitas
    private val _labelsIntensitas = MutableLiveData<ArrayList<String>>()
    val labelsIntensitas:LiveData<ArrayList<String>> get() = _labelsIntensitas

    private val _dataChartPeakKonsumsi = MutableLiveData<BarData>()
    val dataChartPeakKonsumsi: LiveData<BarData> get() = _dataChartPeakKonsumsi
    private val _labelsPeakKonsumsi = MutableLiveData<ArrayList<String>>()
    val labelsPeakKonsumsi:LiveData<ArrayList<String>> get() = _labelsPeakKonsumsi

    private val _dataChartSubmeter = MutableLiveData<PieData>()
    val dataChartSubmeter: LiveData<PieData> get() = _dataChartSubmeter
    private val _labelsSubmeter = MutableLiveData<ArrayList<String>>()
    val labelsSubmeter:LiveData<ArrayList<String>> get() = _labelsSubmeter


    fun callApiGetPowerUsage(userId: Int, startDate: String, endDate: String){
        apiService.getPowerUsage(userId, startDate, endDate).enqueue(object : Callback<GetPowerUsageResponse>{
            override fun onResponse(
                call: Call<GetPowerUsageResponse>,
                response: Response<GetPowerUsageResponse>
            ) {
                if (response.isSuccessful){
                    val data = response.body()
                    _powerUsageData.postValue(data!!.data)
                } else{
                    Log.e("Error:", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetPowerUsageResponse>, t: Throwable) {
                Log.e("Error:", "onFailure: ${t.message}")
            }
        })
    }

    fun updateChartDataTrenKonsumsiHarian(lineData: LineData) {
        _dataChartTrenKonsumsiHarian.postValue(lineData)
    }
    fun saveLabelsTrenKonsumsiHarian(labels: ArrayList<String>){
        _labelsTrenKonsumsiHarian.postValue(labels)
    }

    fun updateChartDataVoltage(barData: BarData) {
        _dataChartVoltage.postValue(barData)
    }
    fun saveLabelsVoltage(labels: ArrayList<String>){
        _labelsVolatge.postValue(labels)
    }

    fun updateChartDataPeakKonsumsi(barData: BarData) {
        _dataChartPeakKonsumsi.postValue(barData)
    }
    fun saveLabelsPeakKonsumsi(labels: ArrayList<String>){
        _labelsPeakKonsumsi.postValue(labels)
    }

    fun updateChartDataIntensitas(barData: BarData) {
        _dataChartIntensitas.postValue(barData)
    }
    fun saveLabelsIntensitas(labels: ArrayList<String>){
        _labelsIntensitas.postValue(labels)
    }

    fun updateChartDataSubmeter(pieData: PieData) {
        _dataChartSubmeter.postValue(pieData)
    }
    fun saveLabelsSubmeter(labels: ArrayList<String>){
        _labelsSubmeter.postValue(labels)
    }



}