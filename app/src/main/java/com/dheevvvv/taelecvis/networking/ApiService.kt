package com.dheevvvv.taelecvis.networking

import com.dheevvvv.taelecvis.model.Data
import com.dheevvvv.taelecvis.model.UserPostLoginRequest
import com.dheevvvv.taelecvis.model.UserPostRequest
import com.dheevvvv.taelecvis.model.UserPostResponse
import com.dheevvvv.taelecvis.model.power_usage.GetPowerUsageResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("users/register")
    fun registerUser(
        @Body request : UserPostRequest
    ) : Call<Data>

    @POST("users/login")
    fun loginUser(
        @Body request : UserPostLoginRequest
    ) : Call<Data>

    @GET("power-usage-by-range-date")
    fun getPowerUsage(
        @Query("userId") userId:Int,
        @Query("startDate") startDate:String,
        @Query("endDate") endDate:String
    ) : Call<GetPowerUsageResponse>

}