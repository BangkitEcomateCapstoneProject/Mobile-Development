package com.example.ecomate.Api

import com.google.gson.annotations.SerializedName

data class StoreUserRequest(

    @SerializedName("userId")
    val userId: String,

    @SerializedName("email")
    val email: String
)
