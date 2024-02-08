package com.dheevvvv.taelecvis.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    object RetrofitClient {
        private const val BASE_URL ="http://localhost:8080/api/"
        private const val API_KEY = "DHEVA"

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(API_KEY))
            .build()


        val instance : ApiService by lazy {
            val retrofit= Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(ApiService::class.java)
        }
    }
}