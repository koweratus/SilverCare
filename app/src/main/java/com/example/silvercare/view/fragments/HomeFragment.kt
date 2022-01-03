package com.example.silvercare.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.silvercare.R
import com.example.silvercare.databinding.FragmentHomeBinding
import com.example.silvercare.model.Caretaker
import com.example.silvercare.model.User
import com.example.silvercare.utils.Constants
import com.example.silvercare.view.activities.MainActivity
import com.example.silvercare.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private val viewModel by activityViewModels<LoginViewModel>()
    val args by navArgs<HomeFragmentArgs>()

    private val userCollectionReference = Firebase.firestore.collection(Constants.USERS)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveUser()
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
            checkUser()
        }*/
    }


    private fun checkUser() {
        val sharedPreferences =
            this.activity
                ?.getSharedPreferences(Constants.SILVERCARE_PREFERENCES, Context.MODE_PRIVATE)
        val email = sharedPreferences?.getString(Constants.CARETAKER_EMAIL, "")!!
        val gson = Gson()
        val json = sharedPreferences.getString(Constants.CARETAKER_DETAILS, "")
        val user = gson.fromJson(json, Caretaker ::class.java)
        //get current user
        val firebaseUser = fAuth.currentUser
        if (firebaseUser == null) {
            //logged out

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()


        } else {
            //logged in
            binding.userId = fAuth.currentUser!!.uid
            binding.mobile = user.email
        }
    }

    private fun retrieveUser() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val sharedPreferences =
                requireActivity().getSharedPreferences(Constants.SILVERCARE_PREFERENCES, Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString(Constants.CARETAKER_DETAILS, "")
            val user = gson.fromJson(json, Caretaker ::class.java)

            val querySnapshot = userCollectionReference.get().await()
            val sb = StringBuilder()
            for(document in querySnapshot.documents) {
                val person = document.toObject<User>()
                sb.append("$person\n")

            }
            withContext(Dispatchers.Main) {
                if (sb.toString().split("=", ",")[11] == FirebaseAuth.getInstance().currentUser!!.uid) {
                    binding.tvDobOfSenior.text = sb.toString().split("=", ",")[5]
                    binding.tvNameOfSenior.text = sb.toString().split("=", ",")[3]
                    binding.tvWelcome.text =  getString(R.string.welcome, user.username)
                }
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }


    }

}