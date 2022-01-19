package com.example.silvercare.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.example.silvercare.viewmodel.LoginViewModel
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

    private lateinit var binding: FragmentHomeBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var firebaseUser: FirebaseUser? = null

    val TAG = "HomeFragment"

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
        firebaseUser = FirebaseAuth.getInstance().currentUser
        FirebaseService.sharedPref =
            requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener{
            FirebaseService.token = it
            saveToken(it)
        }

        return binding.root
    }

    private fun saveToken(token: String) {
        val db = FirebaseFirestore.getInstance()
        val noteRef = db.collection(Constants.USERS).document(firebaseUser!!.uid.toString())
        FirebaseService.token = token
        noteRef.update("token", token)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        retrieveUser()

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        binding.btnReminderPill.setOnClickListener {
            FirebaseFirestore.getInstance().collection(Constants.CARETAKERS)
                .document(firebaseUser!!.uid).get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val friendId = documentSnapshot.getString("friend").toString()
                    val username = documentSnapshot.getString("username").toString()
                    addNotification(friendId,username)

                }
            }
        }
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
        val user = gson.fromJson(json, Caretaker::class.java)
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
                requireActivity().getSharedPreferences(
                    Constants.SILVERCARE_PREFERENCES,
                    Context.MODE_PRIVATE
                )
            val gson = Gson()
            val json = sharedPreferences.getString(Constants.CARETAKER_DETAILS, "")
            val user = gson.fromJson(json, Caretaker::class.java)

            val querySnapshot = userCollectionReference.get().await()
            val sb = StringBuilder()
            for (document in querySnapshot.documents) {
                val person = document.toObject<User>()
                sb.append("$person\n")

            }
            withContext(Dispatchers.Main) {
                if (sb.toString()
                        .split("=", ",")[11] == FirebaseAuth.getInstance().currentUser!!.uid
                ) {
                    //[1] user id
                    binding.tvDobOfSenior.text = sb.toString().split("=", ",")[5]
                    binding.tvNameOfSenior.text = sb.toString().split("=", ",")[3]
                    binding.tvWelcome.text = getString(R.string.welcome, user.username)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun addNotification(userId: String, username: String) {
        //osoba koja ce primiti obavijest
        val notiRef = FirebaseDatabase.getInstance().reference.child("Notifications")
            .child(userId)

        val notiMap = HashMap<String, Any>()
        // osoba koja lajka post
        notiMap["userId"] = firebaseUser!!.uid
        notiMap["text"] = requireContext().getString(R.string.pill_schedule_reminder).toString()

        notiRef.push().setValue(notiMap)

        FirebaseFirestore.getInstance().collection(Constants.USERS).document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val title = "Pill Reminder"
                    val message = String.format(resources.getString(R.string.pill_schedule_reminder_push),username)
                    val recipientToken = documentSnapshot.getString("token").toString()
                    if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {
                        PushNotification(
                            NotificationData(title, message),
                            recipientToken
                        ).also {
                            sendNotification(it)
                        }
                    }
                }
            }


    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d(TAG, "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e(TAG, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

}