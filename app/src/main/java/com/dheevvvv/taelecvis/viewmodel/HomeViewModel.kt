package com.dheevvvv.taelecvis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import com.dheevvvv.taelecvis.model.power_usage.Data
import com.dheevvvv.taelecvis.model.power_usage.GetPowerUsageResponse
import com.dheevvvv.taelecvis.networking.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(val userManager: UserManager, val apiService: ApiService): ViewModel() {
    private val _powerUsageData = MutableLiveData<List<Data>>()
    val powerUsageData: LiveData<List<Data>> get() = _powerUsageData


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
}