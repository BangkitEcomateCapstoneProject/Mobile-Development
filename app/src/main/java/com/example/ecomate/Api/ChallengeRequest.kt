package com.example.ecomate.Api

import com.google.gson.annotations.SerializedName

data class ChallengeRequest(

	@SerializedName("challengePoints")
	var challengePoints: Int,

	@SerializedName("challengeDesc")
	var challengeDesc: String,

	@SerializedName("challengeTitle")
	var challengeTitle: String,

	@SerializedName("challengeStatus")
	var challengeStatus: String
)
