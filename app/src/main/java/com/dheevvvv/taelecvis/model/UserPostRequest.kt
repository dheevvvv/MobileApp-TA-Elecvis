package com.dheevvvv.taelecvis.model

import com.google.gson.annotations.SerializedName

data class UserPostRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username:String,
    @SerializedName("phone_number")
    val phoneNumber: String
)
