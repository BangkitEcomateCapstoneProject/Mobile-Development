package com.example.ecomate.UI.Login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.ecomate.Api.ApiConfigChallenge
import com.example.ecomate.Api.ApiConfigDatabase
import com.example.ecomate.Api.ApiService
import com.example.ecomate.Api.ChallengeRequest
import com.example.ecomate.Api.StoreUserRequest
import com.example.ecomate.R
import com.example.ecomate.Response.ChallengeListResponseItem
import com.example.ecomate.Response.UserChallengeIdResponse
import com.example.ecomate.Response.UserChallengesResponse
import com.example.ecomate.Response.UserIdResponse
import com.example.ecomate.UI.Main.MainActivity
import com.example.ecomate.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LoginActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "GoogleActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val databaseApiService: ApiService = ApiConfigDatabase.getApiService()
    private val challengeApiService: ApiService = ApiConfigChallenge.getApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setupView()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // The user is already signed in, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
        }

        binding.buttonGoogle.setOnClickListener {
            Log.d("Login Activity OnCreate", "Google Clicked")
            login()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showLoading(true)

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        lifecycleScope.launch {
                            try {
                                userDataSync(user.uid)
                                showLoading(false)
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } catch (e: Exception) {
                                Log.e("LoginActivity", "Error in userDataSync: ${e.message}")
                                // Handle error appropriately, e.g., show a message to the user
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun login() {
        val credentialManager = CredentialManager.create(this)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private suspend fun userDataSync(userId: String) {
        return suspendCancellableCoroutine { continuation ->
            val client = databaseApiService.getUserById(userId)
            client.enqueue(object : Callback<UserIdResponse> {
                override fun onResponse(
                    call: Call<UserIdResponse>,
                    response: Response<UserIdResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            if (responseBody.error) {
                                val userEmail = auth.currentUser!!.email
                                storeUser(userId, userEmail!!)
                            }
                            lifecycleScope.launch {
                                try {
                                    challengeDataSync(userId)
                                    continuation.resume(Unit)
                                } catch (e: Exception) {
                                    continuation.resumeWithException(e)
                                }
                            }
                        } else {
                            continuation.resume(Unit)
                        }
                    } else {
                        Log.e("UserDataSync", "onFailure: ${response.message()}")
                        continuation.resumeWithException(Exception("UserDataSync failed: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<UserIdResponse>, t: Throwable) {
                    Log.e("UserDataSync", "onFailure: ${t.message}")
                    continuation.resumeWithException(t)
                }
            })

            continuation.invokeOnCancellation {
                client.cancel()
            }
        }
    }

    private fun storeUser(userId: String, userEmail: String) {
        val client = databaseApiService.storeUser(StoreUserRequest(userId, userEmail))
        client.enqueue(object : Callback<UserIdResponse> {
            override fun onResponse(
                call: Call<UserIdResponse>,
                response: Response<UserIdResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.w("Store User", responseBody.toString())
                    }
                } else {
                    Log.e("UserDataSync", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserIdResponse>, t: Throwable) {
                Log.e("UserDataSync", "onFailure: ${t.message}")
            }
        })
    }

    private suspend fun challengeDataSync(userId: String) {
        return suspendCancellableCoroutine { continuation ->
            Log.w("UserDataSync", "challengeDataSync called from UserDataSync")

            val client = databaseApiService.getUserChallenges(userId)
            client.enqueue(object : Callback<UserChallengesResponse> {
                override fun onResponse(
                    call: Call<UserChallengesResponse>,
                    response: Response<UserChallengesResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.challengeList.isEmpty()) {
                            lifecycleScope.launch {
                                try {
                                    getChallengeList(userId)
                                    continuation.resume(Unit)
                                } catch (e: Exception) {
                                    continuation.resumeWithException(e)
                                }
                            }
                        } else {
                            continuation.resume(Unit)
                        }
                    } else {
                        Log.e("UserDataSync", "onFailure: ${response.message()}")
                        continuation.resumeWithException(Exception("ChallengeDataSync failed: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<UserChallengesResponse>, t: Throwable) {
                    Log.e("UserDataSync", "onFailure: ${t.message}")
                    continuation.resumeWithException(t)
                }
            })

            continuation.invokeOnCancellation {
                client.cancel()
            }
        }
    }

    private suspend fun addChallengeList(userId: String, request: ChallengeRequest) {
        return suspendCancellableCoroutine { continuation ->
            Log.w("GetChallengeList", "addChallengeList called from getChallengeList")

            val client = databaseApiService.createChallenge(userId, request)
            client.enqueue(object : Callback<UserChallengeIdResponse> {
                override fun onResponse(
                    call: Call<UserChallengeIdResponse>,
                    response: Response<UserChallengeIdResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Log.e("Add Challenge to user", "success")
                        }
                        continuation.resume(Unit)
                    } else {
                        Log.e("Add Challenge to user", "onFailure: ${response.message()}")
                        continuation.resumeWithException(Exception("Add Challenge to user failed: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<UserChallengeIdResponse>, t: Throwable) {
                    Log.e("Add Challenge to user", "onFailure: ${t.message}")
                    continuation.resumeWithException(t)
                }
            })

            continuation.invokeOnCancellation {
                client.cancel()
            }
        }
    }

    private suspend fun getChallengeList(userId: String) {
        return suspendCancellableCoroutine { continuation ->
            Log.w("ChallengeDataSync", "getChallengeList called from challengeDataSync")

            val client = challengeApiService.getChallengeList()
            client.enqueue(object : Callback<List<ChallengeListResponseItem>> {
                override fun onResponse(
                    call: Call<List<ChallengeListResponseItem>>,
                    response: Response<List<ChallengeListResponseItem>>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.isNotEmpty()) {
                            lifecycleScope.launch {
                                try {
                                    for (challenge in responseBody) {
                                        Log.w("Challenge List", responseBody.size.toString())
                                        val request = ChallengeRequest(
                                            challengeDesc = challenge.challengeDesc,
                                            challengePoints = challenge.challengePoints,
                                            challengeStatus = challenge.challengeStatus,
                                            challengeTitle = challenge.challengeTitle
                                        )
                                        addChallengeList(userId, request)
                                    }
                                    continuation.resume(Unit)
                                } catch (e: Exception) {
                                    continuation.resumeWithException(e)
                                }
                            }
                        } else {
                            continuation.resume(Unit)
                        }
                    } else {
                        Log.e("Get Challenge List", "onFailure: ${response.message()}")
                        continuation.resumeWithException(Exception("Get Challenge List failed: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<List<ChallengeListResponseItem>>, t: Throwable) {
                    Log.e("Get Challenge List", "onFailure: ${t.message}")
                    continuation.resumeWithException(t)
                }
            })

            continuation.invokeOnCancellation {
                client.cancel()
            }
        }
    }

    private fun showLoading(state: Boolean) { binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE }

}


