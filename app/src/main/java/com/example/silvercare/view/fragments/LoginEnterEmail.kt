package com.example.silvercare.view.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.silvercare.R
import com.example.silvercare.databinding.FragmentLoginEnterEmailBinding
import com.example.silvercare.databinding.LoginShowQrcodeBinding
import com.example.silvercare.utils.BaseActivity
import com.example.silvercare.view.activities.HomeActivity
import com.example.silvercare.viewmodel.LoginViewModel
import java.util.regex.Pattern

class LoginEnterEmail : BaseActivity() {

    private lateinit var binding: FragmentLoginEnterEmailBinding
    private val viewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginEnterEmailBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel

        binding.btnFinish.setOnClickListener {
            if (validateEmail() ){
                viewModel.email.value = binding.etEmail.text.toString()
                viewModel.getTaskResult().observe(viewLifecycleOwner, { taskId ->
                    taskId?.let {
                        viewModel.insertEmail(taskId)
                    }
                })
                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                activity?.finish()
            }else{
                validateEmail()
            }

        }
    }
    private fun validateEmail(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.error_email), true,requireActivity())
                false
            }

            !isValidEmail(binding.etEmail.text.toString()) ->{
                showErrorSnackBar(resources.getString(R.string.error_email_format), true,requireActivity())
                false
            }

            else -> {
                true
            }
        }
    }
    private fun isValidEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}