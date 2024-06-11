package com.dheevvvv.taelecvis.grafikvisualisasi


import com.dheevvvv.taelecvis.DummyData
import com.dheevvvv.taelecvis.view.HomeFragment
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateGrafikKonsumsiListrikUseCaseTest {

    private lateinit var fragment: HomeFragment

    @Before
    fun setUp() {
        fragment = HomeFragment()
    }

    @Test
    fun `test calculateTotalConsumption`() {
        val dataList = DummyData.getPowerUsageData()

        val startDate = "2024-06-02"
        val endDate = "2024-06-03"
        val expectedTotalConsumption = 30.2f + 30.2f + 15.5f

        val actualTotalConsumption = fragment.calculateTotalConsumption(dataList, startDate, endDate)
        assertEquals(expectedTotalConsumption, actualTotalConsumption, 0.01f)
    }

    @Test
    fun `test calculateDailyAverages`() {
        val dataList = DummyData.getPowerUsageData()

        val startDate = "2024-06-02"
        val endDate = "2024-06-03"
        val expectedDailyAverages = listOf(
            (30.2f + 30.2f) / 2,
            15.5f
        )

        val actualDailyAverages = fragment.calculateDailyAverages(dataList, startDate, endDate)
        assertEquals(expectedDailyAverages, actualDailyAverages)
    }

    @Test
    fun `test calculatePeakConsumption`() {
        val dataList = DummyData.getPowerUsageData()

        val result = fragment.calculatePeakConsumption(dataList, "2024-06-01", "2024-06-01")

        // Verifikasi hasilnya
        assertEquals(20.0f, result[0]) // Puncak konsumsi pada jam 0 tanggal 2024-06-01 harusnya 20.0
        assertEquals(29.7f, result[4])
    }


    @Test
    fun `test calculateMaxGlobalIntensity`() {
        // Data referensi
        val dataList = DummyData.getPowerUsageData()

        // Rentang tanggal untuk diuji
        val startDate = "2024-06-01"
        val endDate = "2024-06-01"

        // Panggil fungsi yang diuji
        val result = fragment.calculateMaxGlobalIntensity(dataList, startDate, endDate)

        // Verifikasi hasil
        assertEquals(5, result.size)
        assertEquals(5.8f, result[4])
    }

    @Test
    fun `test calculateAverageVoltage`() {
        // Data referensi
        val dataList = DummyData.getPowerUsageData()

        // Rentang tanggal untuk diuji
        val startDate = "2024-06-01"
        val endDate = "2024-06-01"

        // Panggil fungsi yang diuji
        val result = fragment.calculateAverageVoltage(dataList, startDate, endDate)
        val expected = (220.5f+220.5f+221.0f)/3
        // Verifikasi hasil
        assertEquals(5, result.size)
        assertEquals(expected, result[0]) //verifikasi untuk jam ke-0

    }

}