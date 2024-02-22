package com.dheevvvv.taelecvis.networking

import com.dheevvvv.taelecvis.model.UserPostLoginRequest
import com.dheevvvv.taelecvis.model.UserPostRequest
import com.dheevvvv.taelecvis.model.UserPostResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("users/register")
    fun registerUser(
        @Body request : UserPostRequest
    ) : Call<UserPostResponse>

    @POST("users/login")
    fun loginUser(
        @Body request : UserPostLoginRequest
    ) : Call<Any>

}