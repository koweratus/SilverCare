package com.example.silvercare.model

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.silvercare.utils.Constants
import com.example.silvercare.utils.LogInFailedState
import com.example.silvercare.utils.Utils.toast
import com.example.silvercare.view.activities.MainActivity
import com.example.silvercare.viewmodel.LoginViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthAppRepository @Inject constructor(
    @ActivityRetainedScoped val actContxt: MainActivity,
    @ApplicationContext val context: Context
) {

    private val verificationId: MutableLiveData<String> = MutableLiveData()

    val credential: MutableLiveData<PhoneAuthCredential> = MutableLiveData()

    private val taskResult: MutableLiveData<Task<AuthResult>> = MutableLiveData()

    private val failedState: MutableLiveData<LogInFailedState> = MutableLiveData()

    private val mobile: MutableLiveData<LoginViewModel> = MutableLiveData()

    private val auth = FirebaseAuth.getInstance()

    private lateinit var activity: Activity

    fun sendOtp(activity: Activity, country: Country, mobile: String) {
        val number = country.noCode + " " + mobile
        this.activity = activity
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(listener)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    taskResult.value = task


                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException)
                        toast(context, "Invalid verification code!")
                    failedState.value = LogInFailedState.SignIn
                }
            }
    }

    fun setCredential(credential: PhoneAuthCredential) {
        signInWithPhoneAuthCredential(credential)
    }

    fun getVCode(): MutableLiveData<String> {
        return verificationId
    }

    fun setVCodeNull() {
        verificationId.value = null
    }

    fun clearOldAuth() {
        credential.value = null
        taskResult.value = null
    }

    fun getCredential(): LiveData<PhoneAuthCredential> {
        return credential
    }

    fun getTaskResult(): LiveData<Task<AuthResult>> {
        return taskResult
    }

    fun getFailed(): LiveData<LogInFailedState> {
        return failedState
    }

    private val listener = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("TAG", "onVerificationCompleted:$credential")
            this@AuthAppRepository.credential.value = credential
            Handler().postDelayed({
                signInWithPhoneAuthCredential(credential)
            }, 1000)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            failedState.value = LogInFailedState.Verification
            Log.e("TAG", "onVerificationFailed: ${e.message}")
            when (e) {
                is FirebaseAuthInvalidCredentialsException ->
                    toast(context, "Invalid Request")
                else -> toast(context, e.message.toString())
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verificationId, token)
            Log.d("TAG", "onCodeSent:$verificationId")
            this@AuthAppRepository.verificationId.value = verificationId
            toast(context, "Verification code sent successfully")
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)
        }
    }


}


