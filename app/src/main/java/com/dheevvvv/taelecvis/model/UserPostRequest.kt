package com.dheevvvv.taelecvis.model

import com.google.gson.annotations.SerializedName

data class UserPostRequest(
    @SerializedName("name")
    var name:String,

    @SerializedName("username")
    var username: String,

    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String,

    @SerializedName("phone_number")
    var phoneNumber: String

)
