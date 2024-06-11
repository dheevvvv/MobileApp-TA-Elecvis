package com.dheevvvv.taelecvis

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.dheevvvv.taelecvis.datastore_preferences.UserManager
import com.dheevvvv.taelecvis.model.power_usage.Data
import com.dheevvvv.taelecvis.model.power_usage.GetPowerUsageResponse
import com.dheevvvv.taelecvis.networking.ApiService
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@RunWith(MockitoJUnitRunner::class)
class CallApiTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var userManager: UserManager

    @Mock
    lateinit var mockCall: Call<GetPowerUsageResponse>

    lateinit var homeViewModel: HomeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        homeViewModel = HomeViewModel(userManager, apiService)
    }

    @Test
    fun `test callApiGetPowerUsage success`() {
        val mockResponse = GetPowerUsageResponse(1, DummyData.getPowerUsageData(), "200")
        val response = Response.success(mockResponse)
        whenever(apiService.getPowerUsage(eq(10), anyString(), anyString())).thenReturn(mockCall)


        doAnswer { invocation ->
            val callback: Callback<GetPowerUsageResponse> = invocation.getArgument(0)
            callback.onResponse(mockCall, response)
            null
        }.`when`(mockCall).enqueue(any())

        // Observe LiveData
        val observer = mock<Observer<List<Data>>>()
        homeViewModel.powerUsageData.observeForever(observer)

        // Panggil fungsi yang diuji
        homeViewModel.callApiGetPowerUsage(10, "2006-11-2", "2006-11-9")

        // Verifikasi bahwa data yang diterima dari respons API disimpan dengan benar dalam LiveData powerUsageData
        verify(observer).onChanged(mockResponse.data)
        assertEquals(mockResponse.data, homeViewModel.powerUsageData.value)
    }

}