package com.example.ecomate.UI.TrashHub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecomate.Api.ApiConfigTrashHub
import com.example.ecomate.Response.TrashHubResponse
import com.example.ecomate.Response.TrashHubResponseItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrashHubViewModel: ViewModel() {
    val listTrashHub = MutableLiveData<ArrayList<TrashHubResponseItem>>()

    fun getTrashHub() {
        ApiConfigTrashHub.getApiService().getTrashHub(
        ).enqueue( object : Callback<TrashHubResponse> {
            override fun onResponse(call: Call<TrashHubResponse>, response: Response<TrashHubResponse>) {
                if (response.isSuccessful) {
                    listTrashHub.postValue(response.body()?.trashHubResponse)
                }
            }
            override fun onFailure(call: Call<TrashHubResponse>, t: Throwable) {
            }

        })
    }

    fun getListTrashHub(): LiveData<ArrayList<TrashHubResponseItem>> {
        return listTrashHub
    }
}