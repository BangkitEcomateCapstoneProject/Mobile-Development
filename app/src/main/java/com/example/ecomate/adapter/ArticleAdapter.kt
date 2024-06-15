package com.example.ecomate.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomate.Response.ArticleResponse
import com.example.ecomate.databinding.ItemArticleBinding

class ArticleAdapter : ListAdapter<ArticleResponse, ArticleAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    class MyViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleResponse) {
            binding.tvArticleTitle.text = article.title
            binding.tvArticleContent.text = article.cleanContent
            binding.cardArticle.setOnClickListener {
                val url = article.articleLink
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                binding.root.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleResponse>() {
            override fun areItemsTheSame(
                oldItem: ArticleResponse,
                newItem: ArticleResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ArticleResponse,
                newItem: ArticleResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}