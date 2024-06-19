package com.example.ecomate.UI.Challenge

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecomate.Api.ApiConfigDatabase
import com.example.ecomate.Response.UserChallengesResponse
import com.example.ecomate.adapter.ChallengeAdapter
import com.example.ecomate.databinding.FragmentPageChallengeBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeFragment : Fragment() {
    private lateinit var binding: FragmentPageChallengeBinding
    private lateinit var challengeAdapter: ChallengeAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var radioInProgress: RadioButton
    private lateinit var radioCompleted: RadioButton
    private lateinit var radioNotStarted: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPageChallengeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvChallenge.layoutManager = LinearLayoutManager(activity)
        challengeAdapter = ChallengeAdapter()
        binding.rvChallenge.adapter = challengeAdapter

        radioNotStarted = binding.radioNotStarted
        radioInProgress = binding.radioInProgress
        radioCompleted = binding.radioCompleted

        getNotStartedChallenge()

        radioNotStarted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getNotStartedChallenge()
            }
        }

        radioInProgress.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getInProgressChallenge()
            }
        }

        radioCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getCompletedChallenge()
            }
        }
    }

    private fun getNotStartedChallenge() {
        showLoading(true)
        val currentUser = auth.currentUser

        val client = ApiConfigDatabase.getApiService().getUserChallenges(currentUser!!.uid)
        client.enqueue(object : Callback<UserChallengesResponse> {
            override fun onResponse(
                call: Call<UserChallengesResponse>,
                response: Response<UserChallengesResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        challengeAdapter.submitList(responseBody.challengeList)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UserChallengesResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getInProgressChallenge() {
        showLoading(true)
        val currentUser = auth.currentUser

        val client = ApiConfigDatabase.getApiService().getUserChallenges(currentUser!!.uid)
        client.enqueue(object : Callback<UserChallengesResponse> {
            override fun onResponse(
                call: Call<UserChallengesResponse>,
                response: Response<UserChallengesResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        challengeAdapter.submitList(responseBody.challengeList)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UserChallengesResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getCompletedChallenge() {
        showLoading(true)
        val currentUser = auth.currentUser

        val client = ApiConfigDatabase.getApiService().getUserChallenges(currentUser!!.uid)
        client.enqueue(object : Callback<UserChallengesResponse> {
            override fun onResponse(
                call: Call<UserChallengesResponse>,
                response: Response<UserChallengesResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        challengeAdapter.submitList(responseBody.challengeList)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<UserChallengesResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(state: Boolean) { binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE }

    companion object {
        private const val TAG = "ChallengeFragment"
        const val ARG_CHALLENGE_STATUS = "challenge_status"
        const val ARG_SECTION_NUMBER = "section_number"
    }
}