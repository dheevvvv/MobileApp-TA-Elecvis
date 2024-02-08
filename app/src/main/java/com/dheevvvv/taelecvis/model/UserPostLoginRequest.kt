package com.dheevvvv.taelecvis.model

import com.google.gson.annotations.SerializedName

data class UserPostLoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)
