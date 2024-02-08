package com.dheevvvv.taelecvis.model


import com.google.gson.annotations.SerializedName

data class UserGetResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<DataX>,
    @SerializedName("status")
    val status: String
)