package com.example.ecomate.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomate.Api.ApiConfigDatabase
import com.example.ecomate.Api.ChallengeStatusRequest
import com.example.ecomate.Response.ChallengeListItem
import com.example.ecomate.Response.UserChallengeIdResponse
import com.example.ecomate.databinding.ItemChallengeNotStartedBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotStartedChallengeAdapter(private val refreshChallenges: () -> Unit)  : ListAdapter<ChallengeListItem, NotStartedChallengeAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemChallengeNotStartedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, refreshChallenges)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val challenge = getItem(position)
        holder.bind(challenge)
    }

    class MyViewHolder(
        private val binding: ItemChallengeNotStartedBinding,
        private val refreshChallenges: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(challenge: ChallengeListItem) {
            binding.tvChallengeName.text = challenge.challengeTitle
            binding.tvChallengeDesc.text = challenge.challengeDesc
            binding.tvChallengePoint.text = challenge.challengePoints.toString()
            binding.btnAddChallenge.setOnClickListener {
                updateChallengeStatus(challenge.challengeId)
            }
        }

        private fun updateChallengeStatus(challengeId: Int) {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val client = ApiConfigDatabase.getApiService().updateChallengeStatus(
                    currentUser.uid,
                    challengeId,
                    ChallengeStatusRequest("inProgress")
                )
                client.enqueue(object : Callback<UserChallengeIdResponse> {
                    override fun onResponse(
                        call: Call<UserChallengeIdResponse>,
                        response: Response<UserChallengeIdResponse>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(itemView.context, "Challenge Status Updated", Toast.LENGTH_SHORT).show()
                            refreshChallenges()
                        } else {
                            Log.e(TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<UserChallengeIdResponse>, t: Throwable) {
                        Log.e(TAG, "onFailure: ${t.message}")
                    }
                })
            }
        }

        companion object {
            private const val TAG = "MyViewHolder"
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChallengeListItem>() {
            override fun areItemsTheSame(
                oldItem: ChallengeListItem,
                newItem: ChallengeListItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ChallengeListItem,
                newItem: ChallengeListItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}