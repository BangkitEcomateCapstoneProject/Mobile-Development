package com.example.ecomate.UI.Login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.ecomate.Api.ApiConfigDatabase
import com.example.ecomate.Api.ApiService
import com.example.ecomate.Api.StoreUserRequest
import com.example.ecomate.R
import com.example.ecomate.Response.LoginResult
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "GoogleActivity"
    }
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var loginPreferences: LoginPreferences
    private val databaseApiService: ApiService = ApiConfigDatabase.getApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setupView()
        val currentUser = auth.currentUser
        loginPreferences = LoginPreferences(this)

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
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Save user info to shared preferences
                        val loginResult = LoginResult(user.uid, user.displayName, user.getIdToken(false).result?.token)
                        loginPreferences.setLogin(loginResult)
                        userDataSync(user.uid)
                    }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
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
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
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

    private fun userDataSync(userId: String) {
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
}


