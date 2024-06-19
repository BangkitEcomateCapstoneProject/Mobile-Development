package com.example.ecomate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomate.Response.ChallengeListItem
import com.example.ecomate.data.ViewType
import com.example.ecomate.databinding.ItemChallengeCompletedBinding
import com.example.ecomate.databinding.ItemChallengeInProgressBinding
import com.example.ecomate.databinding.ItemChallengeNotStartedBinding

class ChallengeAdapter : ListAdapter<ChallengeListItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    override fun getItemViewType(position: Int): Int {
        // Implement logic to return different view types based on position or data
        val item = getItem(position)
        return when (item.challengeStatus) {
            "notStarted" -> ViewType.NOT_STARTED.type
            "InProgress" -> ViewType.IN_PROGRESS.type
            "completed" -> ViewType.COMPLETED.type
            else -> ViewType.NOT_STARTED.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.NOT_STARTED.type -> {
                val binding = ItemChallengeNotStartedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolderNotStarted(binding)
            }
            ViewType.IN_PROGRESS.type -> {
                val binding = ItemChallengeInProgressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolderInProgress(binding)
            }
            ViewType.COMPLETED.type -> {
                val binding = ItemChallengeCompletedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolderCompleted(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val challenge = getItem(position)
        when (holder) {
            is ViewHolderNotStarted -> holder.bind(challenge)
            is ViewHolderInProgress -> holder.bind(challenge)
            is ViewHolderCompleted -> holder.bind(challenge)
        }
    }

    class ViewHolderNotStarted(private val binding: ItemChallengeNotStartedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(challenge: ChallengeListItem) {
            binding.tvChallengeName.text = challenge.challengeTitle
            binding.tvChallengeDesc.text = challenge.challengeDesc
            binding.btnAddChallenge.setOnClickListener {
                // Handle button click
            }
        }
    }

    class ViewHolderInProgress(private val binding: ItemChallengeInProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(challenge: ChallengeListItem) {
            binding.tvChallengeName.text = challenge.challengeTitle
            binding.tvChallengeDesc.text = challenge.challengeDesc
            binding.btnCheckChallenge.setOnClickListener {
                // Handle button click
            }
            binding.btnRemoveChallenge.setOnClickListener {

            }
        }
    }

    class ViewHolderCompleted(private val binding: ItemChallengeCompletedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(challenge: ChallengeListItem) {
            binding.tvChallengeName.text = challenge.challengeTitle
            binding.tvChallengeDesc.text = challenge.challengeDesc
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