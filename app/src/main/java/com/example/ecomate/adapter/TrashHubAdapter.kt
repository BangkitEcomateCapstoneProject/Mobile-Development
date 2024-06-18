package com.example.ecomate.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomate.Response.TrashHubResponseItem
import com.example.ecomate.databinding.RvTrashHubBinding
import java.util.ArrayList

class TrashHubAdapter: ListAdapter<TrashHubResponseItem, TrashHubAdapter.TrashHubViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrashHubViewHolder {
        return TrashHubViewHolder(
            RvTrashHubBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class TrashHubViewHolder(private val binding: RvTrashHubBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trashHub: TrashHubResponseItem) {
            with(binding) {
                tvTrashHubName.text = trashHub.namaBakSampah
                tvTrashHubLocation.text = trashHub.alamat
            }
            Log.d("binding TRASHHUBBB", trashHub.namaBakSampah.toString())
        }
    }

    override fun onBindViewHolder(holder: TrashHubViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrashHubResponseItem>() {
            override fun areItemsTheSame(
                oldItem: TrashHubResponseItem,
                newItem: TrashHubResponseItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: TrashHubResponseItem,
                newItem: TrashHubResponseItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}