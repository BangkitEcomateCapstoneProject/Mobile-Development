package com.example.ecomate.Response

import com.google.gson.annotations.SerializedName

data class TrashHubResponse(

	@field:SerializedName("TrashHubResponse")
	val trashHubResponse: ArrayList<TrashHubResponseItem>
)

data class TrashHubResponseItem(

	@field:SerializedName("Alamat")
	val alamat: String? = null,

	@field:SerializedName("Nama_Bak_Sampah")
	val namaBakSampah: String? = null
)
