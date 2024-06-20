package com.example.ecomate.UI.ResultPredict

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecomate.Api.AddDetectionRequest
import com.example.ecomate.Api.ApiConfigAI
import com.example.ecomate.Api.ApiConfigArticle
import com.example.ecomate.Api.ApiConfigDatabase
import com.example.ecomate.Api.ApiService
import com.example.ecomate.Api.ArticleRequest
import com.example.ecomate.R
import com.example.ecomate.Response.ArticleResponseItem
import com.example.ecomate.Response.ErrorResponse
import com.example.ecomate.Response.PredictResponse
import com.example.ecomate.adapter.ArticleAdapter
import com.example.ecomate.data.Result
import com.example.ecomate.databinding.ActivityResultPredictBinding
import com.example.ecomate.getImageUri
import com.example.ecomate.reduceFileImage
import com.example.ecomate.uriToFile
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "ResultPredictActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()

        binding.btnOk.setOnClickListener { finish() }

        setupRecyclerView()
        startCamera()

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
                                        val prediction = result.data.prediction.toString()
                                        val probability: Double = result.data.probability as Double
                                        val thresholds = 0.8

                                        if (probability >= thresholds) {
                                            binding.imgPreviewfoto.setImageURI(currentImageUri)
                                            binding.textViewResultPredict.text = prediction

                                            getArticles(result.data.prediction!!)
                                            addDetectionData(AddDetectionRequest(prediction, probability))
                                        } else {
                                            AlertDialog.Builder(this@ResultPredictActivity).apply {
                                                setTitle("Prediction Failed!")
                                                setMessage("We are unable to process your photo. Please provide clearer photos.")
                                                setPositiveButton(R.string.ok) { _, _ ->
                                                    finish()
                                                }
                                                create()
                                                show()
                                            }
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

    private fun getArticles(result: String) {
        showLoading(true)
        val text = "How to recycle $result"

        val client = articleApiService.ArticleRecomend(ArticleRequest(text))
        client.enqueue(object : Callback<List<ArticleResponseItem>> {
            override fun onResponse(
                call: Call<List<ArticleResponseItem>>,
                response: Response<List<ArticleResponseItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        articleAdapter.submitList(responseBody)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ArticleResponseItem>>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun addDetectionData(request: AddDetectionRequest) {
        val currentUser = auth.currentUser

        val client =
            ApiConfigDatabase.getApiService().addDetectionData(currentUser!!.uid, request)
        client.enqueue(object : Callback<ErrorResponse> {
            override fun onResponse(
                call: Call<ErrorResponse>,
                response: Response<ErrorResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.w(TAG, "onSuccess: ${response.message()}")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ErrorResponse>, t: Throwable) {
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