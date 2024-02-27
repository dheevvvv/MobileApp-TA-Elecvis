package com.dheevvvv.taelecvis.model.power_usage


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("date")
    val date: String,
    @SerializedName("globalActivePower")
    val globalActivePower: Double,
    @SerializedName("globalIntensity")
    val globalIntensity: Double,
    @SerializedName("globalReactivePower")
    val globalReactivePower: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("subMetering1")
    val subMetering1: Double,
    @SerializedName("subMetering2")
    val subMetering2: Double,
    @SerializedName("subMetering3")
    val subMetering3: Double,
    @SerializedName("time")
    val time: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("voltage")
    val voltage: Double
)