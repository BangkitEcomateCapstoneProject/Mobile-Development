package com.example.ecomate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomate.Response.ArticleResponseItem
import com.example.ecomate.Response.ChallengeListItem
import com.example.ecomate.Response.UserChallengesResponse
import com.example.ecomate.databinding.FragmentPageChallengeBinding
import com.example.ecomate.databinding.FragmentRvChallengeBinding
import com.example.ecomate.databinding.ItemArticleBinding
import com.example.ecomate.databinding.RvChallengeBinding

class ChallengeAdapter : ListAdapter<ChallengeListItem, ChallengeAdapter.ChallengeViewHolder >(DIFF_CALLBACK) {
    class ChallengeViewHolder(private val binding: RvChallengeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(challenge: UserChallengesResponse){

        }

    }



    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = RvChallengeBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ChallengeViewHolder(binding)
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