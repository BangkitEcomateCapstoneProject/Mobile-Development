package com.example.ecomate.UI.TrashHub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecomate.Api.ApiConfigTrashHub
import com.example.ecomate.Api.ApiService
import com.example.ecomate.Response.TrashHubResponseItem
import com.example.ecomate.adapter.TrashHubAdapter
import com.example.ecomate.databinding.FragmentTrashHubBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrashHubFragment : Fragment() {
    private val trashHubApiService: ApiService = ApiConfigTrashHub.getApiService()
    private lateinit var adapter: TrashHubAdapter
    private lateinit var binding: FragmentTrashHubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrashHubBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvTrashHub.layoutManager = LinearLayoutManager(activity)
        adapter = TrashHubAdapter()
        binding.rvTrashHub.adapter = adapter
        getTrashHub()
        binding.searchBar.clearFocus()
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchBar.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchTrashHub(newText)
                }
                return false
            }

        })



    }


    private fun searchTrashHub(query: String){
        val client = trashHubApiService.getSearchTrashHub(query)
        client.enqueue(object : Callback<List<TrashHubResponseItem>> {
            override fun onResponse(
                call: Call<List<TrashHubResponseItem>>,
                response: Response<List<TrashHubResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        adapter.submitList(responseBody)
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

    private fun getTrashHub() {
        showLoading(true)
        val client = trashHubApiService.getTrashHub()

        client.enqueue(object : Callback<List<TrashHubResponseItem>> {
            override fun onResponse(
                call: Call<List<TrashHubResponseItem>>,
                response: Response<List<TrashHubResponseItem>>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        adapter.submitList(responseBody)
                    }else{
                        Log.e("RESPONSE ERROR", "Response unsuccessful: ${response.errorBody()?.string()}")
                    }
                }
            }
            override fun onFailure(call: Call<List<TrashHubResponseItem>>, t: Throwable) {
                showLoading(false)
                Log.e("API CALL FAILURE", "Failed to fetch TrashHub data", t)
            }
        })
    }

    private fun showLoading(state: Boolean) { binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE }

}