package com.example.ecomate.Api

import com.example.ecomate.Response.ArticleResponse
import com.example.ecomate.Response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST("predict")
    suspend fun Predict(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ):PredictResponse
    @FormUrlEncoded
    @POST("articleRecomend")
    suspend fun ArticleRecomend(
        @Field("text") text: String
    ):ArticleResponse


}