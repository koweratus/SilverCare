package com.example.silvercare.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.silvercare.model.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class SharedViewModel @Inject
constructor(@ApplicationContext private val context: Context) : ViewModel() {

    val country = MutableLiveData<Country>()

    fun setCountry(country: Country) {
        this.country.value = country
    }

    override fun onCleared() {
        super.onCleared()
    }
}