package com.example.ecomate.Api

import com.google.gson.annotations.SerializedName

data class ChallengeStatusRequest(

    @SerializedName("challengeStatus")
    val challengeStatus: String
)
