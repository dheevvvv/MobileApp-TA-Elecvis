package com.dheevvvv.taelecvis.model


import com.google.gson.annotations.SerializedName

data class UserPostResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("status")
    val status: String
)