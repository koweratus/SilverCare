package com.example.silvercare.viewmodel

import android.app.Application
import android.app.ProgressDialog
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.silvercare.model.AuthAppRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class LoginViewModel(application: Application) : AndroidViewModel(application) {


    private val authAppRepository: AuthAppRepository
    val userLiveData: MutableLiveData<FirebaseUser?>

    //if code sending fails, we will resend

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private val context = getApplication<Application>().applicationContext
    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerifcationId: String? = null

    private val TAG = "MAIN_TAG"
    private lateinit var progressDialog: ProgressDialog

    fun login(){
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted: ")
                authAppRepository.signInWithPhoneAuthCredential(phoneAuthCredential)

            }

            override fun onVerificationFailed(e: FirebaseException) {

                progressDialog.dismiss()
                Log.d(TAG, "onVerificationFailed: ${e.message}")
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verficationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent: $verficationId")
                mVerifcationId = verficationId
                forceResendingToken = token
                progressDialog.dismiss()

                Log.d(TAG, "onCodeSent: $verficationId")

                Toast.makeText(context, "Verification code sent...", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}

    init {
        authAppRepository = AuthAppRepository(application)
        userLiveData = authAppRepository.userLiveData
    }
}