package com.example.silvercare.view

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.silvercare.databinding.FragmentLoginOtpBinding
import com.example.silvercare.databinding.FragmentLoginPhoneNumberBinding
import com.example.silvercare.viewmodel.LoginViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class LoginOtpFragment : Fragment() {

    private val sharedViewModel: LoginViewModel by activityViewModels()
    private var binding: FragmentLoginOtpBinding? = null


    //if code sending fails, we will resend
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerifcationId: String? = null
    private lateinit var auth: FirebaseAuth

    private val TAG = "MAIN_TAG"

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentLoginOtpBinding.inflate(inflater,container,false)
        binding = fragmentBinding
        auth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)



        fragmentBinding.btnCodeVerify.setOnClickListener {
            //input verification code
            val code = fragmentBinding.etCode.text.toString().trim()
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(requireContext(), "Please enter verification code", Toast.LENGTH_SHORT)
                    .show()
            } else {
                verifyPhoneNumberWithCode(mVerifcationId!!, code)
            }



        }

        fragmentBinding.tvResendCode.setOnClickListener {
            val phone = sharedViewModel.userLiveData.toString().trim()
            //validate phone number
            Log.d(TAG, "resendVerificationCode: $phone")
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(requireContext(), "Please enter phone number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                resendVerification(phone, forceResendingToken!!)
            }
        }

        return fragmentBinding.root
    }

    private fun resendVerification(phone: String, token: PhoneAuthProvider.ForceResendingToken) {
        progressDialog.setMessage("Resending Code...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks!!)
            .setActivity(requireActivity())
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
                Toast.makeText(requireContext(), " Logged in as${phone}", Toast.LENGTH_SHORT)
                    .show()

                //start profile activity


            }
            .addOnFailureListener {
                //login fail
                    e ->
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
            loginOtpFragment = this@LoginOtpFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}