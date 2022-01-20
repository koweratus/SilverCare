package com.example.silvercare.viewmodel

import android.app.Activity
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.example.silvercare.R
import com.example.silvercare.utils.LogInFailedState
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt


class HomeViewModel : ViewModel() {

    var count = MutableLiveData<Int>(0)

    fun setCount(brojac: Int) {
        count.value = brojac
    }

    @JvmName("getCount1")
    fun getCount(): MutableLiveData<Int> {
        return count
    }
}