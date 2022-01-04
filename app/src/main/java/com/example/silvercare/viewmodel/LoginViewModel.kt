package com.example.silvercare.viewmodel

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.silvercare.R
import com.example.silvercare.model.AuthAppRepository
import com.example.silvercare.model.Caretaker
import com.example.silvercare.model.Country
import com.example.silvercare.model.User
import com.example.silvercare.utils.Constants
import com.example.silvercare.utils.LogInFailedState
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@HiltViewModel
class LoginViewModel @Inject
constructor(
    @ApplicationContext private val context: Context,
    private val authRepo: AuthAppRepository
) :
    ViewModel() {

    val country = MutableLiveData<Country>()

    val mobile = MutableLiveData<String>()

    val type = MutableLiveData<Boolean>()

    val caretakerName = MutableLiveData<String>()

    val caretakerDob = MutableLiveData<String>()

    val seniorName = MutableLiveData<String>()

    val seniorDob = MutableLiveData<String>()

    val email = MutableLiveData<String>()

    val userProfileGot = MutableLiveData("")

    private val progress = MutableLiveData(false)

    val isProfileCompleted = MutableLiveData(false)

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

    var lastRequestedMobile = ""

    private lateinit var activity: Activity

    private lateinit var timer: CountDownTimer

    private val fireStore = FirebaseFirestore.getInstance()


    fun setCountry(country: Country) {
        this.country.value = country
    }

    fun setSeniorName(name: String) {
        seniorName.value = name
    }

    fun setCaretakerName(name: String) {
        caretakerName.value = name
    }

    fun setUserType(tip: Boolean) {
        type.value = tip
    }

    fun setSeniorDob(dob: String) {
        seniorDob.value = dob
    }

    fun sendOtp(activity: Activity) {
        authRepo.clearOldAuth()
        this.activity = activity
        lastRequestedMobile = "${country.value?.noCode} ${mobile.value}"
        authRepo.sendOtp(activity, country.value!!, mobile.value!!)
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

    fun setEmptyText() {
        otpOne.value = ""
        otpTwo.value = ""
        otpThree.value = ""
        otpFour.value = ""
        otpFive.value = ""
        otpSix.value = ""
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

    fun setVCodeNull() {
        verifyCode = authRepo.getVCode().value!!
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

    fun insertEmail(taskId: Task<AuthResult>) {
        val firebaseUser: FirebaseUser = taskId.result.user!!
        val db = FirebaseFirestore.getInstance()
        val noteRef = db.collection(Constants.CARETAKERS).document(firebaseUser.uid.toString())

        noteRef.update("email", email.value.toString())

        val caretaker = Caretaker(
            username = caretakerName.value.toString(),
            mobile = mobile.value.toString(),
            email = email.value.toString(),
            dob = caretakerDob.value.toString()
        )

        getCaretakerDetails(caretaker)

    }

    fun fetchUser(taskId: Task<AuthResult>, type: Boolean) {
        val firebaseUser: FirebaseUser = taskId.result.user!!

        if (type) {
            val user = Caretaker(
                firebaseUser.uid,
                mobile = mobile.value.toString(),
                email = email.value.toString(),
                profileCompleted = false
            )

            val db = FirebaseFirestore.getInstance()
            val noteRef = db.collection(Constants.CARETAKERS).document(firebaseUser.uid)
            noteRef.set(user).addOnSuccessListener { doc ->

                noteRef.set(user, SetOptions.merge())
                    .addOnSuccessListener { data ->
                        setVProgress(false)
                        progress.value = false

                        //if (data.exists()) {  //already created user
                        //save profile in preference
                        // START
                        val sharedPreferences =
                            activity.getSharedPreferences(
                                Constants.SILVERCARE_PREFERENCES,
                                Context.MODE_PRIVATE
                            )

                        // Create an instance of the editor which is help us to edit the SharedPreference.
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString(
                            Constants.USER_DETAILS,
                            "${user.username} ${user.mobile}"
                        )
                        editor.apply()
                        // END

                        // }
                        userProfileGot.value = firebaseUser.uid
                    }.addOnFailureListener { e ->
                        setVProgress(false)
                        progress.value = false
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }

            }

        } else {
            val user = User(
                firebaseUser.uid,
                mobile = mobile.value.toString(),
                profileCompleted = false

            )

            val db = FirebaseFirestore.getInstance()
            val noteRef = db.collection(Constants.USERS).document(firebaseUser.uid.toString())
            noteRef.set(user, SetOptions.merge())
                .addOnSuccessListener { data ->
                    setVProgress(false)
                    progress.value = false

                    userProfileGot.value = firebaseUser.uid
                }.addOnFailureListener { e ->
                    setVProgress(false)
                    progress.value = false
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }


    }


    fun deleteCaretakersDocument(taskId: Task<AuthResult>) {
        val firebaseUser: FirebaseUser = taskId.result.user!!
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.CARETAKERS).document(firebaseUser.uid)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    fun checkIfUsersAreConnected(taskId: Task<AuthResult>) {
        val firebaseUser: FirebaseUser = taskId.result.user!!
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.CARETAKERS).document(firebaseUser.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                var friend: String
                if (documentSnapshot.exists()) {
                    friend = documentSnapshot.getString("friend")!!
                    isProfileCompleted.value = friend.isNotEmpty()
                } else {
                    Toast.makeText(context, R.string.users_connected, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, R.string.users_not_connected, Toast.LENGTH_SHORT).show()
            }
    }

    fun updateIsProfileCompleted(taskId: Task<AuthResult>, type: String) {
        val firebaseUser: FirebaseUser = taskId.result.user!!


        val db = FirebaseFirestore.getInstance()
        val noteRef = db.collection(type).document(firebaseUser.uid)
        noteRef.update("profileCompleted", isProfileCompleted.value).addOnSuccessListener {
            setVProgress(false)
            progress.value = false
        }.addOnFailureListener { e ->
            setVProgress(false)
            progress.value = false
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun checkIfProfileIsCompleted(taskId: FirebaseUser?, type: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection(type).document(taskId?.uid!!).get()
            .addOnSuccessListener { documentSnapshot ->
                var friend: Boolean
                if (documentSnapshot.exists()) {
                    friend = documentSnapshot.getBoolean("profileCompleted")!!
                    isProfileCompleted.value = friend
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, R.string.users_not_connected, Toast.LENGTH_SHORT).show()
            }
    }

    fun fetchSeniorUser(taskId: Task<AuthResult>, name: String) {
        val firebaseUser: FirebaseUser = taskId.result.user!!
        val fname: String = name.split(";")[0]
        val dob: String = name.split(";")[1]
        val id: String = name.split(";")[2]
        val cmobile: String = name.split(";")[3]
        val cname: String = name.split(";")[4]
        val cdob: String = name.split(";")[5]

        val user = User(
            firebaseUser.uid,
            username = fname,
            dob = dob,
            friend = id,
            mobile = mobile.value.toString(),
            profileCompleted = true
        )

        val db = FirebaseFirestore.getInstance()
        val noteRef = db.collection(Constants.USERS).document(firebaseUser.uid)

        noteRef.set(user, SetOptions.merge())
            .addOnSuccessListener { data ->
                setVProgress(false)
                progress.value = false

                userProfileGot.value = firebaseUser.uid
            }.addOnFailureListener { e ->
                setVProgress(false)
                progress.value = false
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }

        // Save preferences
        getUserDetails(user)

        val caretaker = Caretaker(
            id = id,
            mobile = cmobile,
            friend = firebaseUser.uid,
            username = cname,
            dob = cdob,
            profileCompleted = false

        )

        val ref = db.collection(Constants.CARETAKERS).document(id)
        ref.set(caretaker, SetOptions.merge())
            .addOnSuccessListener { data ->
                setVProgress(false)
                progress.value = false
            }.addOnFailureListener { e ->
                setVProgress(false)
                progress.value = false
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    private fun getUserDetails(user : User) {
        //save profile in preference
        // START
        val sharedPreferences =
            activity.getSharedPreferences(
                Constants.SILVERCARE_PREFERENCES,
                Context.MODE_PRIVATE
            )

        // Create an instance of the editor which is help us to edit the SharedPreference.
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        editor.putString(
            Constants.USER_DETAILS,
            json
        )
        editor.apply()
        // END

    }

    private fun getCaretakerDetails(caretaker : Caretaker) {
        //save profile in preference
        // START
        val sharedPreferences =
            activity.getSharedPreferences(
                Constants.SILVERCARE_PREFERENCES,
                Context.MODE_PRIVATE
            )

        // Create an instance of the editor which is help us to edit the SharedPreference.
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(caretaker)
        editor.putString(
            Constants.CARETAKER_DETAILS,
            json
        )
        editor.apply()
        // END

    }

    fun clearAll() {
        userProfileGot.value = null
        authRepo.clearOldAuth()
    }



}