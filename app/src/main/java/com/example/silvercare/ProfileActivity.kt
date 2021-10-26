package com.example.silvercare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.silvercare.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding:ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        checkUser()

        //logout the user
        binding.btnLogout.setOnClickListener{
            auth.signOut()
            checkUser()
        }
        setContentView(binding.root)
    }

    private fun checkUser() {
        //get current user
        val firebaseUser = auth.currentUser
        if ( firebaseUser == null){
            //logged out
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else{
            //logged in
            val phone = firebaseUser.phoneNumber
            //set phone number
            binding.tvProfile.text = phone
        }
    }
}