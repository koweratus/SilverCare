package com.example.silvercare.model

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.os.Build
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.silvercare.view.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class AuthAppRepository(private val application: Application) {
    private val firebaseAuth: FirebaseAuth
    val userLiveData: MutableLiveData<FirebaseUser?>
    val loggedOutLiveData: MutableLiveData<Boolean>

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken?

    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks?
    private var mVerifcationId: String?
    //progress dialog
    private lateinit var progressDialog: ProgressDialog
    private val TAG = "MAIN_TAG"


    fun startPhoneNumberVerification(phone: String) {

        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(Activity().parent)
            .setCallbacks(callbacks!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    fun resendVerification(phone: String, token: PhoneAuthProvider.ForceResendingToken) {
        progressDialog.setMessage("Resending Code...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks!!)
            .setActivity(Activity().parent)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        progressDialog.setMessage("Verifying Code...")
        progressDialog.show()
        Log.d(TAG, "verifyPhoneNumberWithCode: $verificationId")
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

     fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Logging In")
        Log.d(TAG, "loginWithCredential")
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser!!.phoneNumber
                Toast.makeText(application.applicationContext, " Logged in as${phone}", Toast.LENGTH_SHORT)
                    .show()

                //start profile activity


            }
            .addOnFailureListener {
                //login fail
                    e ->
                progressDialog.dismiss()
                Toast.makeText(application.applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun logOut() {
        firebaseAuth.signOut()
        loggedOutLiveData.postValue(true)
    }

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        userLiveData = MutableLiveData()
        loggedOutLiveData = MutableLiveData()
        forceResendingToken = null
        mVerifcationId = null
        callbacks = null
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
            loggedOutLiveData.postValue(false)
        }
    }
}