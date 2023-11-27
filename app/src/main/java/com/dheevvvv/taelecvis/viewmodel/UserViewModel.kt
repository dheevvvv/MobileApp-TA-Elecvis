package com.dheevvvv.taelecvis.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import kotlinx.coroutines.launch

class UserViewModel(val userManager: UserManager):ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email


    fun getUsername() {
        viewModelScope.launch {
            val username = userManager.getUsername()
            _username.postValue(username)
        }
    }
}