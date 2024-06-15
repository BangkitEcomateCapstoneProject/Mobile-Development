package com.example.ecomate.UI.ResultPredict

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.ecomate.Api.ApiConfigAI
import com.example.ecomate.Api.ApiService
import com.example.ecomate.R
import com.example.ecomate.data.Result
import com.example.ecomate.Response.PredictResponse
import com.example.ecomate.UI.Login.LoginPreferences
import com.example.ecomate.createCustomTempFile
import com.example.ecomate.databinding.ActivityResultPreddictBinding
import com.example.ecomate.reduceFileImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ResultPreddictActivity( private val loginPreferences: LoginPreferences) : AppCompatActivity() {

    private val apiService: ApiService = ApiConfigAI.getApiService()

    private lateinit var binding: ActivityResultPreddictBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding = ActivityResultPreddictBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        startTakePhoto()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@ResultPreddictActivity,
                "com.example.ecomate",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val photo = BitmapFactory.decodeFile(myFile.path)
            val image = convertImage()
            addPhoto(image).observe(this@ResultPreddictActivity){ result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
//                            showLoading(true)
                        }
                        is Result.Error -> {
//                            showLoading(false)
//                            showAlert(
//                                getString(R.string.post_fail_1),
//                                getString(R.string.post_fail_2)
//                            )
//                            { }
                        }
                        is Result.Success -> {
//                            showLoading(false)
//                            binding.imgPreviewfoto.setImageBitmap(currentPhotoPath)
                        }
                    }
                }
            }

        }
    }
    private fun convertImage(): MultipartBody.Part {
        val file = reduceFileImage(getFile as File)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
    }
    fun addPhoto(
        imageFile: MultipartBody.Part,
    ): LiveData<Result<PredictResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.Predict(
                token = "Bearer ${loginPreferences.getUser().token}",
                file = imageFile
            )
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }



    }
}