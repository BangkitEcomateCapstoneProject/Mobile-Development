package com.example.ecomate.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomate.Response.TrashHubResponseItem
import com.example.ecomate.databinding.ItemTrashHubBinding

class TrashHubAdapter: ListAdapter<TrashHubResponseItem, TrashHubAdapter.TrashHubViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashHubViewHolder {
        val binding =
            ItemTrashHubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrashHubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrashHubViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    class TrashHubViewHolder(private val binding: ItemTrashHubBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trashHub: TrashHubResponseItem) {
            binding.tvTrashHubName.text = trashHub.namaBakSampah
            Log.d("binding item", trashHub.namaBakSampah)
            binding.tvTrashHubLocation.text = trashHub.alamat
        }
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