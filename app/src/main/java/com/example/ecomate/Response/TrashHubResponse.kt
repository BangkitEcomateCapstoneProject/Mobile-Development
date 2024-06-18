package com.example.ecomate.Response

import com.google.gson.annotations.SerializedName

data class TrashHubResponse(

	@field:SerializedName("TrashHubResponse")
	val trashHubResponse: List<TrashHubResponseItem>
)

data class TrashHubResponseItem(

	@field:SerializedName("Alamat")
	val alamat: String,

	@field:SerializedName("Nama_Bak_Sampah")
	val namaBakSampah: String
)
