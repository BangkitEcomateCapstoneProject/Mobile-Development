package com.example.ecomate.UI.TrashHub

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecomate.R
import com.example.ecomate.UI.Login.LoginPreferences
import com.example.ecomate.databinding.FragmentTrashHubBinding
import org.checkerframework.common.returnsreceiver.qual.This

class TrashHubFragment : Fragment(R.layout.fragment_trash_hub) {
    private lateinit var binding: FragmentTrashHubBinding
    private lateinit var trashHubViewModel: TrashHubViewModel
    private lateinit var preference: LoginPreferences
    private lateinit var adapter: TrashHubAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrashHubBinding.bind(view)
        adapter = TrashHubAdapter()
        preference = LoginPreferences(requireContext())
        adapter.notifyDataSetChanged()


        trashHubViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TrashHubViewModel::class.java)

        val TOKEN: String = preference.getString(LoginPreferences.USER_ID).toString()

        Log.d("TOKENNN",TOKEN)
        showList()
        binding.apply {
            rvTrashHub.setHasFixedSize(true)
            rvTrashHub.layoutManager = LinearLayoutManager(activity)
            rvTrashHub.adapter = adapter
        }




    }
    private fun getTrashHub() {
        trashHubViewModel.getTrashHub()
    }
    private fun showList() {
        getTrashHub()

        trashHubViewModel.getListTrashHub().observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.setList(it)
            }
        }
    }








}
