package com.example.silvercare.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.example.silvercare.R
import com.example.silvercare.databinding.FragmentHomeBinding
import com.example.silvercare.model.Caretaker
import com.example.silvercare.model.NotificationData
import com.example.silvercare.model.PushNotification
import com.example.silvercare.model.User
import com.example.silvercare.service.FirebaseService
import com.example.silvercare.service.FirebaseService.Companion.token
import com.example.silvercare.utils.Constants
import com.example.silvercare.utils.RetrofitInstance
import com.example.silvercare.view.activities.MainActivity
import com.example.silvercare.viewmodel.HomeViewModel
import com.example.silvercare.viewmodel.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

const val TOPIC = "/topics/myTopic"

class HomeFragment : Fragment() {

    private val viewModel by activityViewModels<LoginViewModel>()
    val args by navArgs<HomeFragmentArgs>()

    private lateinit var binding: FragmentHomeBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var firebaseUser: FirebaseUser? = null
    private val homeViewModel by activityViewModels<HomeViewModel>()

    val TAG = "HomeFragment"


    private val userCollectionReference = FirebaseFirestore.getInstance().collection(Constants.USERS)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveUser()
        val sharedPreferences =
            requireActivity().getSharedPreferences(
                Constants.SILVERCARE_PREFERENCES,
                Context.MODE_PRIVATE
            )
        val type = sharedPreferences.getString(Constants.USER_TYPE, "")
        if (type == "Caretaker"){
            binding.cv.visibility = View.VISIBLE
            binding.tvMonitor.visibility = View.VISIBLE
            binding.btnPillDrank.visibility = View.GONE
        }else{
            binding.cv.visibility = View.GONE
            binding.tvMonitor.visibility = View.GONE
            binding.btnPillDrank.visibility = View.VISIBLE


        }
    }



    private fun retrieveUser() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val sharedPreferences =
                requireActivity().getSharedPreferences(
                    Constants.SILVERCARE_PREFERENCES,
                    Context.MODE_PRIVATE
                )
            val gson = Gson()
            val type = sharedPreferences.getString(Constants.USER_TYPE, "")
            val json = sharedPreferences.getString(Constants.CARETAKER_DETAILS, "")
            val user = gson.fromJson(json, Caretaker::class.java)

            val querySnapshot = userCollectionReference.get().await()
            val sb = StringBuilder()
            for (document in querySnapshot.documents) {
                val person = document.toObject<User>()
                sb.append("$person\n")

            }
            withContext(Dispatchers.Main) {
                if (type == "Caretaker"){
                if (sb.toString().split("=", ",")[11]== FirebaseAuth.getInstance().currentUser!!.uid
                ) {
                    //[1] user id
                    binding.tvDobOfSenior.text = sb.toString().split("=", ",")[5]
                    binding.tvNameOfSenior.text = sb.toString().split("=", ",")[3]
                    binding.tvWelcome.text = getString(R.string.welcome, user.username)
                }

            }else{
                    binding.tvWelcome.text = getString(R.string.welcome,sb.toString().split("=", ",")[3])
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }


    }



}