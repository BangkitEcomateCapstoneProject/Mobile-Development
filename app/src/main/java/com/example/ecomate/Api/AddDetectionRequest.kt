package com.example.ecomate.Api

import com.google.gson.annotations.SerializedName

data class AddDetectionRequest(
    @SerializedName("prediction")
    val prediction: String,

    @SerializedName("probability")
    val probability: Double
)
