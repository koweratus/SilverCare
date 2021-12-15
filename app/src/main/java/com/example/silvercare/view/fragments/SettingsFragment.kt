package com.example.silvercare.view.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.silvercare.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}