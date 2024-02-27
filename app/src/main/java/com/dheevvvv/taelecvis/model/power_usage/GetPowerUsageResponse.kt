package com.dheevvvv.taelecvis.model.power_usage


import com.google.gson.annotations.SerializedName

data class GetPowerUsageResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: String
)