package com.example.silvercare.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.silvercare.view.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var fAuth: FirebaseAuth

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(com.example.silvercare.R.xml.root_preferences, rootKey)
        fAuth = FirebaseAuth.getInstance()

        /*  binding.btnLogOut.setOnClickListener {
      findNavController().navigate(R.id.action_FHome_to_FLogin)
  }
  checkUser()
  //logout the user
  binding.btnLogOut.setOnClickListener {
      viewModel.getTaskResult().observe(viewLifecycleOwner, { taskId ->
          taskId?.let {
              viewModel.deleteCaretakersDocument(taskId)
          }
      })
      fAuth.signOut()

  }*/

        val logoutButton: Preference? = findPreference("logout")
        logoutButton!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            signout()
            true
        }

    }

    private fun signout(){
        fAuth.signOut()
        activity?.finishAffinity()
        startActivity(Intent(activity,MainActivity::class.java))
    }
}