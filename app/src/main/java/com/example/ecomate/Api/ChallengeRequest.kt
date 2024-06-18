package com.example.ecomate.Api

import com.google.gson.annotations.SerializedName

data class ChallengeRequest(

	@SerializedName("challengePoints")
	val challengePoints: Int,

	@SerializedName("challengeDesc")
	val challengeDesc: String,

	@SerializedName("challengeTitle")
	val challengeTitle: String,

	@SerializedName("challengeStatus")
	val challengeStatus: String
)
