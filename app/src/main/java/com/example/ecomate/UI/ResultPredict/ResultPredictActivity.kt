package com.example.ecomate.UI.ResultPredict

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecomate.Api.ApiConfigAI
import com.example.ecomate.Api.ApiConfigArticle
import com.example.ecomate.Api.ApiService
import com.example.ecomate.Api.ArticleRequest
import com.example.ecomate.R
import com.example.ecomate.Response.ArticleResponse
import com.example.ecomate.Response.PredictResponse
import com.example.ecomate.adapter.ArticleAdapter
import com.example.ecomate.data.Result
import com.example.ecomate.databinding.ActivityResultPredictBinding
import com.example.ecomate.getImageUri
import com.example.ecomate.reduceFileImage
import com.example.ecomate.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ResultPredictActivity : AppCompatActivity() {

    private val scanApiService: ApiService = ApiConfigAI.getApiService()
    private val articleApiService: ApiService = ApiConfigArticle.getApiService()

    private lateinit var binding: ActivityResultPredictBinding
    private var currentImageUri: Uri? = null
    private lateinit var articleAdapter: ArticleAdapter

    companion object {
        private const val TAG = "ResultPredictActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        startCamera()
        setupRecyclerView()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()

                showLoading(true)

                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "file",
                    imageFile.name,
                    requestImageFile
                )
                lifecycleScope.launch {
                    try {
                        uploadPhoto(multipartBody).observe(this@ResultPredictActivity) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        showLoading(true)
                                    }
                                    is Result.Error -> {
                                        showLoading(false)
                                        Log.w("ResultPredictActivity", "$result")
                                    }
                                    is Result.Success -> {
                                        showLoading(false)
                                        binding.imgPreviewfoto.setImageURI(currentImageUri)
                                        binding.textViewResultPredict.text = result.data.prediction
                                        lifecycleScope.launch {
                                            getArticles(result.data.prediction!!)
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: HttpException) {
                        Log.d("Scan Activity", e.message())
                    }
                }
            }
        }
    }

    private fun uploadPhoto(
        imageFile: MultipartBody.Part,
    ): LiveData<Result<PredictResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = scanApiService.Predict(
                file = imageFile
            )
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    private suspend fun getArticles(text: String) {
        showLoading(true)
        val client = articleApiService.ArticleRecomend(ArticleRequest(text))
        client.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        articleAdapter.submitList(listOf(responseBody))
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvRecomendationArticle.layoutManager = layoutManager
        articleAdapter = ArticleAdapter()
        binding.rvRecomendationArticle.adapter = articleAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}