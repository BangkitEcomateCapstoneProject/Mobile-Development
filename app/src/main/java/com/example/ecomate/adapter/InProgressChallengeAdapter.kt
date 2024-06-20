package com.example.ecomate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomate.Response.ChallengeListItem
import com.example.ecomate.databinding.ItemChallengeInProgressBinding

class InProgressChallengeAdapter : ListAdapter<ChallengeListItem, InProgressChallengeAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemChallengeInProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val challenge = getItem(position)
        holder.bind(challenge)
    }

    class MyViewHolder(private val binding: ItemChallengeInProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(challenge: ChallengeListItem) {
            binding.tvChallengeName.text = challenge.challengeTitle
            binding.tvChallengeDesc.text = challenge.challengeDesc
            binding.btnCheckChallenge.setOnClickListener {

            }
            binding.btnRemoveChallenge.setOnClickListener {

            }
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