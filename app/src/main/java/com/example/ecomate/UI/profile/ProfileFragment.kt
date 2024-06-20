package com.example.ecomate.UI.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.ecomate.Api.ApiConfigDatabase
import com.example.ecomate.Api.ApiService
import com.example.ecomate.R
import com.example.ecomate.Response.UserIdResponse
import com.example.ecomate.UI.Login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(), View.OnClickListener {
    private val databaseApiService: ApiService = ApiConfigDatabase.getApiService()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = auth.currentUser

        val username: TextView = view.findViewById(R.id.tv_username)
        val imgPhoto: ImageView = view.findViewById(R.id.img_photo)
        val point: TextView = view.findViewById(R.id.tv_total_point)
        val btnLogout: Button = view.findViewById(R.id.btn_logout)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        if (currentUser != null) {
            progressBar.visibility = View.VISIBLE

            val client = databaseApiService.getUserById(currentUser.uid)
            client.enqueue(object : Callback<UserIdResponse> {
                override fun onResponse(
                    call: Call<UserIdResponse>,
                    response: Response<UserIdResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            point.text = responseBody.user.points.toString()
                        }
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserIdResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })

            username.text = currentUser.displayName
            Glide.with(this)
                .load(currentUser.photoUrl)
                .into(imgPhoto)

        }
        btnLogout.setOnClickListener(this)
    }

    companion object {
        private const val TAG = "Profile Fragment"
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.btn_logout) {

            AlertDialog.Builder(requireActivity()).apply {
                setTitle(R.string.logoutTitle)
                setMessage(R.string.logoutMessage)
                setPositiveButton(R.string.logout) { _, _ ->
                    // Sign out from Firebase
                    Firebase.auth.signOut()

                    // Navigate to LoginActivity
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    activity?.finish()
                }
                setNegativeButton(R.string.cancel) { _, _, ->

                }
                create()
                show()
            }
        }
    }
}