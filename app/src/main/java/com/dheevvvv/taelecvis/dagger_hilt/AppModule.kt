package com.dheevvvv.taelecvis.dagger_hilt

import android.app.Application
import com.dheevvvv.taelecvis.database_room.AlertsDAO
import com.dheevvvv.taelecvis.database_room.MainDatabase
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import com.dheevvvv.taelecvis.networking.ApiKeyInterceptor
import com.dheevvvv.taelecvis.networking.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserManager(application: Application): UserManager {
        return UserManager.getInstance(application)
    }

    private const val BASE_URL ="http://192.168.111.102:8080/api/"
    private const val API_KEY = "DHEVA"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor(API_KEY))
        .build()

    @Singleton
    @Provides
    fun provideRetrofitClient(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideApiKeyInterceptor(): ApiKeyInterceptor {
        val apiKey = "DHEVA"
        return ApiKeyInterceptor(apiKey)
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


    @Singleton
    @Provides
    fun providesMainDatabase(application: Application): MainDatabase{
        return MainDatabase.getInstances(application)!!
    }

    @Singleton
    @Provides
    fun providesAlertsDao(alertsDao:MainDatabase): AlertsDAO{
        return alertsDao.alertsDao()
    }
}