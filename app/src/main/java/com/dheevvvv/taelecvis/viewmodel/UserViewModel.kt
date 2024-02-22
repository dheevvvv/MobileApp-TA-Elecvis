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

    private val _registerUser: MutableLiveData<UserPostResponse?> = MutableLiveData()
    val registerUser: MutableLiveData<UserPostResponse?> get() = _registerUser


    fun getUsername() {
        viewModelScope.launch {
            val username = userManager.getUsername()
            _username.postValue(username)
        }
    }

    fun callApiUserPostLogin(email:String, password:String){
        apiService.loginUser(UserPostLoginRequest(email, password)).enqueue(object :
            Callback<Any>{
            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {
                if (response.isSuccessful){
                    val body = response.body().toString()
                    try {
                        val jsonObject = JSONObject(body)
                        val responseObject = Gson().fromJson(body, Data::class.java)
                        _loginUsers.postValue(responseObject)

                    } catch (e:JSONException){
                        _loginUsers.postValue(null)
                    }
                } else{
                    _loginUsers.postValue(null)
                    Log.e("Error:", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                _loginUsers.postValue(null)
                Log.e("Error:", "onFailure: ${t.message}")
            }
        })
    }

    fun callApiUserPostRegister(email: String, password: String, name:String, username:String, phoneNumber:String){
        apiService.registerUser(UserPostRequest(email, password, name, username, phoneNumber)).enqueue(object : Callback<UserPostResponse>{
            override fun onResponse(
                call: Call<UserPostResponse>,
                response: Response<UserPostResponse>
            ) {
                if (response.isSuccessful){
                    val data = response.body()
                    _registerUser.postValue(data!!)
                } else{
                    _registerUser.postValue(null)
                    Log.e("Error:", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserPostResponse>, t: Throwable) {
                _registerUser.postValue(null)
                Log.e("Error:", "onFailure: ${t.message}")
            }

        })
    }
}