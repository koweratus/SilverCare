package com.example.silvercare.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Caretaker(
    val id: String = "",
    val username: String = "",
    val dob: String = "",
    val mobile: String = "",
    val email: String = "",
    val friend: String = "",
    val image: String = "",
    val profileCompleted: Boolean = false
) : Parcelable