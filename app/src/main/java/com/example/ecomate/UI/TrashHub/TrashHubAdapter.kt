package com.example.ecomate.UI.TrashHub

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecomate.Response.TrashHubResponseItem
import com.example.ecomate.databinding.RvTrashHubBinding
import java.util.ArrayList

class TrashHubAdapter: RecyclerView.Adapter<TrashHubAdapter.TrashHubViewHolder>() {
    private val listTrashHub = ArrayList<TrashHubResponseItem>()
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
    @SuppressLint("NotifyDataSetChanged")
    fun setList(stories: ArrayList<TrashHubResponseItem>) {
        listTrashHub.clear()
        listTrashHub.addAll(stories)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = listTrashHub.size

    class TrashHubViewHolder(private val binding: RvTrashHubBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trashHub: TrashHubResponseItem) {
            with(binding) {
                tvTrashHubName.text = trashHub.namaBakSampah
                tvTrashHubLocation.text = trashHub.alamat
            }
        }
    }

    override fun onBindViewHolder(holder: TrashHubViewHolder, position: Int) {
        holder.bind(listTrashHub[position])
    }




    companion object {
    }

}