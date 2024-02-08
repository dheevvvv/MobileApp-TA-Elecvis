package com.dheevvvv.taelecvis.networking

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor (val apiKey:String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url.newBuilder().addQueryParameter("x-api-key", apiKey).build()
        val newRequest = request.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}