package com.example.silvercare

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.silvercare.databinding.ActivityMainBinding
import com.example.silvercare.databinding.DialogCustomImageSelectionBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    //if code sending fails, we will resend

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerifcationId: String? = null
    private lateinit var auth: FirebaseAuth

    private val TAG = "MAIN_TAG"

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        binding.llPhone.visibility = View.VISIBLE
        binding.llCode.visibility = View.GONE
        binding.llFinish.visibility = View.GONE

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted: ")
                signInWithPhoneAuthCredential(phoneAuthCredential)

            }

            override fun onVerificationFailed(e: FirebaseException) {

                progressDialog.dismiss()
                Log.d(TAG, "onVerificationFailed: ${e.message}")
                Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_SHORT).show()
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

                //hide phone layout, show code layout
                binding.llPhone.visibility = View.GONE
                binding.llCode.visibility = View.VISIBLE
                Toast.makeText(this@MainActivity, "Verification code sent...", Toast.LENGTH_SHORT)
                    .show()
                binding.tvCodeDescription.text = "Please type the verification code we sent to ${
                    binding.etPhone.text.toString().trim()
                }"
            }
        }

        binding.btnPhoneContinue.setOnClickListener {

            val phone = binding.etPhone.text.toString().trim()
            //validate phone number
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this@MainActivity, "Please enter phone number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                startPhoneNumberVerification(phone)
            }

        }

        binding.btnCodeVerify.setOnClickListener {
            //input verification code
            val code = binding.etCode.text.toString().trim()
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this@MainActivity, "Please enter verification code", Toast.LENGTH_SHORT)
                    .show()
            } else {
                verifyPhoneNumberWithCode(mVerifcationId!!, code)
            }


        }

        binding.tvResendCode.setOnClickListener {
            val phone = binding.etPhone.text.toString().trim()
            //validate phone number
            Log.d(TAG, "resendVerificationCode: $phone")
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this@MainActivity, "Please enter phone number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                resendVerification(phone, forceResendingToken!!)
            }
        }

        binding.btnFinish.setOnClickListener{
            onRadioButtonClicked()
        }

        binding.ivAddProfilePhoto.setOnClickListener(this@MainActivity)

        setContentView(binding.root)
    }

    private fun startPhoneNumberVerification(phone: String) {

        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun resendVerification(phone: String, token: PhoneAuthProvider.ForceResendingToken) {
        progressDialog.setMessage("Resending Code...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks!!)
            .setActivity(this)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        progressDialog.setMessage("Verifying Code...")
        progressDialog.show()
        Log.d(TAG, "verifyPhoneNumberWithCode: $verificationId")
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Logging In")
        Log.d(TAG, "loginWithCredential")
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                val phone = auth.currentUser!!.phoneNumber
                Toast.makeText(this@MainActivity, " Logged in as${phone}", Toast.LENGTH_SHORT)
                    .show()

                //start profile activity
                binding.llCode.visibility= View.GONE
                binding.llFinish.visibility= View.VISIBLE

            }
            .addOnFailureListener {
                //login fail
                    e ->
                progressDialog.dismiss()
                Toast.makeText(this@MainActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onRadioButtonClicked() {
          if (binding.radioCaretaker.isChecked()){
              startActivity(Intent(this, ProfileActivity::class.java))
              finish()
          }else{
              startActivity(Intent(this, GrannyProfileActivity::class.java))
              finish()
          }




    }
    override fun onClick(v: View) {

        when (v.id) {

            R.id.iv_add_profile_photo -> {

                // TODO Step 6: Replace the Toast Message with the custom dialog.
                // START
                customImageSelectionDialog()
                // END
                return
            }
        }
    }
    private fun customImageSelectionDialog() {
        val dialog = Dialog(this@MainActivity)

        val binding: DialogCustomImageSelectionBinding = DialogCustomImageSelectionBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        dialog.setContentView(binding.root)

        // TODO Step 7: Assign the click for Camera and Gallery. Show the Toast message for now.
        // START
        binding.tvCamera.setOnClickListener {
            Toast.makeText(this@MainActivity, "You have clicked on the Camera.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            Toast.makeText(this@MainActivity, "You have clicked on the Gallery.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        // END

        //Start the dialog and display it on screen.
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.llCode.visibility = View.VISIBLE
    }

}