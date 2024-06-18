package com.example.ecomate.Api

import com.google.gson.annotations.SerializedName

data class AddPointRequest(
    @SerializedName("pointsToAdd")
    val pointToAdd: Int
)
