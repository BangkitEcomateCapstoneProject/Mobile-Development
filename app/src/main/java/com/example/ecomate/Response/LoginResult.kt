package com.example.ecomate.Response

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginResult(
    var userId: String? = null,
    var name: String? = null,
    var token: String? = null
): Parcelable
