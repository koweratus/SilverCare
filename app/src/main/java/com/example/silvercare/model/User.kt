package com.example.silvercare.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val username: String = "",
    val dob: String = "",
    val mobile: String = "",
    val image: String = "",
    val friend: String = "",
    val profileCompleted: Boolean = false
) : Parcelable