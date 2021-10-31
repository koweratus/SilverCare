package com.example.silvercare.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.silvercare.R
import com.example.silvercare.databinding.FragmentHomeBinding
import com.example.silvercare.utils.Constants
import com.example.silvercare.view.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    val args by navArgs<HomeFragmentArgs>()

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

        binding.btnLogOut.setOnClickListener {
            findNavController().navigate(R.id.action_FHome_to_FLogin)
        }
        checkUser()
        //logout the user
        binding.btnLogOut.setOnClickListener {
            fAuth.signOut()
            checkUser()

        }
    }


    private fun checkUser() {
        val sharedPreferences =
            this.getActivity()
                ?.getSharedPreferences(Constants.MYSHOPPAL_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences?.getString(Constants.LOGGED_IN_USERNAME, "")!!
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
            binding.mobile = username
        }
    }

}