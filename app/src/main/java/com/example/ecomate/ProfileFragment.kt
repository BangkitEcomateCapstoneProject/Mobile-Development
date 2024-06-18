package com.example.ecomate

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
import com.example.ecomate.Response.UserIdResponse
import com.example.ecomate.UI.Login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(), View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private val databaseApiService: ApiService = ApiConfigDatabase.getApiService()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        private const val TAG = "Profile Fragment"

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
                setNegativeButton(R.string.batal) { _, _, ->

                }
                create()
                show()
            }
        }
    }
}