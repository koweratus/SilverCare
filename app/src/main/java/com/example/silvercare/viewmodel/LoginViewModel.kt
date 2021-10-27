package com.example.silvercare.viewmodel

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.silvercare.R
import com.example.silvercare.model.AuthAppRepository
import com.example.silvercare.model.Country
import com.example.silvercare.utils.Constants
import com.example.silvercare.utils.LogInFailedState
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

@HiltViewModel
class LoginViewModel @Inject
constructor(@ApplicationContext private val context: Context,
            private val authRepo: AuthAppRepository
) :
    ViewModel() {

    val country = MutableLiveData<Country>()

    val mobile = MutableLiveData<String>()

    val userProfileGot=MutableLiveData("")

    private val progress = MutableLiveData(false)

    private val verifyProgress = MutableLiveData(false)

    var canResend: Boolean = false

    val resendTxt = MutableLiveData<String>()

    val otpOne = MutableLiveData<String>()

    val otpTwo = MutableLiveData<String>()

    val otpThree = MutableLiveData<String>()

    val otpFour = MutableLiveData<String>()

    val otpFive = MutableLiveData<String>()

    val otpSix = MutableLiveData<String>()

    var ediPosition = 0

    var verifyCode: String = ""

    var lastRequestedMobile=""

    private lateinit var activity: Activity

    private lateinit var timer: CountDownTimer

    fun setCountry(country: Country) {
        this.country.value = country
    }

    fun sendOtp(activity: Activity) {
        authRepo.clearOldAuth()
        this.activity=activity
        lastRequestedMobile="${country.value?.noCode} ${mobile.value}"
        authRepo.sendOtp(activity,country.value!!, mobile.value!!)
    }

    fun setProgress(show: Boolean) {
        progress.value = show
    }

    fun getProgress(): LiveData<Boolean> {
        return progress
    }

    fun resendClicked() {
        if (canResend) {
            setVProgress(true)
            sendOtp(activity)
        }
    }

    fun startTimer() {
        try {
            canResend = false
            timer = object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    setTimerTxt(millisUntilFinished / 1000)
                }

                override fun onFinish() {
                    canResend = true
                    resendTxt.value = "Resend"
                }
            }
            timer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resetTimer() {
        canResend = false
        resendTxt.value = ""
        if (this::timer.isInitialized)
            timer.cancel()
    }

    private fun setTimerTxt(seconds: Long) {
        try {
            val s = seconds % 60
            val m = seconds / 60 % 60
            if (s == 0L && m == 0L) return
            val resend: String =
                "Resend in " + String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    m,
                    s
                )
            resendTxt.value = resend
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setEmptyText(){
        otpOne.value=""
        otpTwo.value=""
        otpThree.value=""
        otpFour.value=""
        otpFive.value=""
        otpSix.value=""
    }

    fun setVProgress(show: Boolean) {
        verifyProgress.value = show
    }

    fun getVProgress(): LiveData<Boolean> {
        return verifyProgress
    }

    fun getCredential(): LiveData<PhoneAuthCredential> {
        return authRepo.getCredential()
    }

    fun setCredential(credential: PhoneAuthCredential) {
        setVProgress(true)
        authRepo.setCredential(credential)
    }

    fun setVCodeNull(){
        verifyCode=authRepo.getVCode().value!!
        authRepo.setVCodeNull()
    }

    fun getVerificationId(): MutableLiveData<String> {
        return authRepo.getVCode()
    }

    fun getTaskResult(): LiveData<Task<AuthResult>> {
        return authRepo.getTaskResult()
    }

    fun getFailed(): LiveData<LogInFailedState> {
        return authRepo.getFailed()
    }

    fun fetchUser(taskId: Task<AuthResult>) {
        val db = FirebaseFirestore.getInstance()
        val user = taskId.result?.user
        val noteRef = db.collection("Users").document( user?.uid.toString())
        val map = hashMapOf<String, Any?>()
        map.put("mobile",mobile)
        map.put("country",country)
        noteRef.set(map)
            .addOnSuccessListener { data ->
                setVProgress(false)
                progress.value=false
                //if (data.exists()) {  //already created user
                    //save profile in preference
                // START
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.MYSHOPPAL_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                // Create an instance of the editor which is help us to edit the SharedPreference.
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${mobile.value}"
                )
                editor.apply()
                // END

                // }
                userProfileGot.value=user?.uid
            }.addOnFailureListener { e ->
                setVProgress(false)
                progress.value=false
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun clearAll(){
        userProfileGot.value=null
        authRepo.clearOldAuth()
    }

}