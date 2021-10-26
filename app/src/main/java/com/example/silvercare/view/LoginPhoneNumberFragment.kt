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
import com.example.silvercare.databinding.FragmentLoginPhoneNumberBinding
import com.example.silvercare.viewmodel.LoginViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class LoginPhoneNumberFragment : Fragment() {

    private val sharedViewModel: LoginViewModel by activityViewModels()
    private var binding: FragmentLoginPhoneNumberBinding? = null

    private var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private lateinit var auth: FirebaseAuth

    private val TAG = "MAIN_TAG"
    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentLoginPhoneNumberBinding.inflate(inflater,container,false)
        binding = fragmentBinding
        auth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        binding!!.btnPhoneContinue.setOnClickListener {

            val phone = fragmentBinding.etPhone.text.toString().trim()
            //validate phone number
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(requireContext(), "Please enter phone number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                startPhoneNumberVerification(phone)
            }

        }

        return fragmentBinding.root
    }

    private fun startPhoneNumberVerification(phone: String) {

        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

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
            loginPhoneNumberFragment = this@LoginPhoneNumberFragment
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}