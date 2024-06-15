package com.example.ecomate.Api

import com.example.ecomate.Response.ArticleResponse
import com.example.ecomate.Response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("predict")
    suspend fun Predict(
        @Part file: MultipartBody.Part
    ):PredictResponse

    @POST("recommend")
    suspend fun ArticleRecomend(
        @Body request: ArticleRequest
    ): Call<ArticleResponse>
}