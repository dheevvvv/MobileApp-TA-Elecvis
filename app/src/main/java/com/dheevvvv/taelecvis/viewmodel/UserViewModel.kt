package com.dheevvvv.taelecvis.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import com.dheevvvv.taelecvis.model.Data
import com.dheevvvv.taelecvis.model.UserPostLoginRequest
import com.dheevvvv.taelecvis.model.UserPostRequest
import com.dheevvvv.taelecvis.model.UserPostResponse
import com.dheevvvv.taelecvis.networking.ApiService
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(val userManager: UserManager, val apiService: ApiService):ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _loginUsers: MutableLiveData<Data?> = MutableLiveData()
    val loginUsers: MutableLiveData<Data?> get() = _loginUsers

    private val _registerUser: MutableLiveData<Data> = MutableLiveData()
    val registerUser: MutableLiveData<Data> get() = _registerUser


    fun getUsername() {
        viewModelScope.launch {
            val username = userManager.getUsername()
            _username.postValue(username)
        }
    }


    fun callApiUserPostLogin(email:String, password:String){
        apiService.loginUser(UserPostLoginRequest(email, password)).enqueue(object :
            Callback<Data>{
            override fun onResponse(
                call: Call<Data>,
                response: Response<Data>
            ) {
                if (response.isSuccessful){
                    val body = response.body()
                    _loginUsers.postValue(body)
                } else{
                    _loginUsers.postValue(null)
                    Log.e("Error:", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                _loginUsers.postValue(null)
                Log.e("Error:", "onFailure: ${t.message}")
            }
        })
    }

    fun callApiUserPostRegister(name: String, username: String, email:String, password:String, phoneNumber:String){
        apiService.registerUser(UserPostRequest(name, username, email, password, phoneNumber)).enqueue(object : Callback<Data>{
            override fun onResponse(
                call: Call<Data>,
                response: Response<Data>
            ) {
                if (response.isSuccessful){
                    Log.d("ConnectionStatus", "Connected to API successfully")
                    val data = response.body()
                    _registerUser.postValue(data)
                } else{
                    _registerUser.postValue(null)
                    Log.e("Error:", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                _registerUser.postValue(null)
                Log.e("Error:", "onFailure: ${t.message}")
            }

        })
    }
}