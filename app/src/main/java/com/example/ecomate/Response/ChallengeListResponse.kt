package com.example.ecomate.Response

import com.google.gson.annotations.SerializedName

data class ChallengeListResponse(

	@field:SerializedName("ChallengeListResponse")
	val challengeListResponse: List<ChallengeListResponseItem>
)

data class ChallengeListResponseItem(

	@field:SerializedName("challengePoints")
	val challengePoints: Int,

	@field:SerializedName("challengeDesc")
	val challengeDesc: String,

	@field:SerializedName("challengeTitle")
	val challengeTitle: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("challengeStatus")
	val challengeStatus: String
)
