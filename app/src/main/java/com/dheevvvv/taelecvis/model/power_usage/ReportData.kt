package com.dheevvvv.taelecvis.model.power_usage

data class ReportData(
    val transactionDate: String, val usage: Double, val peakConsumption: Double, val averageIntensity: Double, val averageVoltage: Double, val expenditure: Double
)
