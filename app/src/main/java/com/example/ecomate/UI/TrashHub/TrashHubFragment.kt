package com.example.ecomate.UI.TrashHub

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecomate.Api.ApiConfigTrashHub
import com.example.ecomate.R
import com.example.ecomate.Response.TrashHubResponseItem
import com.example.ecomate.UI.Login.LoginPreferences
import com.example.ecomate.adapter.TrashHubAdapter
import com.example.ecomate.databinding.FragmentTrashHubBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrashHubFragment : Fragment(R.layout.fragment_trash_hub) {
    private lateinit var binding: FragmentTrashHubBinding
//    private lateinit var trashHubViewModel: TrashHubViewModel
    private lateinit var preference: LoginPreferences
    private lateinit var adapter: TrashHubAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrashHubBinding.bind(view)
        adapter = TrashHubAdapter()
        preference = LoginPreferences(requireContext())
//        adapter.notifyDataSetChanged()


//        trashHubViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TrashHubViewModel::class.java)

        val TOKEN: String = preference.getString(LoginPreferences.USER_ID).toString()

        Log.d("TOKENNN",TOKEN)
        binding.apply {
            rvTrashHub.setHasFixedSize(true)
            rvTrashHub.layoutManager = LinearLayoutManager(activity)
            rvTrashHub.adapter = adapter
        }
        getTrashHub()

    }
    private fun getTrashHub() {
        ApiConfigTrashHub.getApiService().getTrashHub()
            .enqueue( object : Callback<List<TrashHubResponseItem>> {
                override fun onResponse(call: Call<List<TrashHubResponseItem>>, response: Response<List<TrashHubResponseItem>>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("RESPONSE BODY", responseBody.toString())
                        if (responseBody != null) {
                            lifecycleScope.launch {
                                withContext(Dispatchers.Main) {
                                    adapter.submitList(responseBody)
                                }
                            }
                        }else{
                            Log.e("RESPONSE ERROR", "Response unsuccessful: ${response.errorBody()?.string()}")
                        }
                    }
            }
            override fun onFailure(call: Call<List<TrashHubResponseItem>>, t: Throwable) {
                Log.e("API CALL FAILURE", "Failed to fetch TrashHub data", t)
            }

        })
    }







}
