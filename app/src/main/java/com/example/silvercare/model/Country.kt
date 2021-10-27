package com.example.silvercare.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Country(
    val code: String, val name: String, val noCode: String,
    val money: String) : Parcelable
