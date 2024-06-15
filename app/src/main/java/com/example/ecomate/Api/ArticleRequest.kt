package com.example.ecomate.Api

import com.google.gson.annotations.SerializedName

data class ArticleRequest(
    @SerializedName("text")
    val text: String
)