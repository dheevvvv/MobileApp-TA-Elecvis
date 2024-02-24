package com.dheevvvv.taelecvis.networking

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor (val apiKey:String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        // Tambahkan header API key
        val modifiedRequest = originalRequest.newBuilder()
            .addHeader("x-api-key", apiKey)
            .build()
        return chain.proceed(modifiedRequest)
    }
}