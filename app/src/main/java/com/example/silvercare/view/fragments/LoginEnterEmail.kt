package com.example.silvercare.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.silvercare.R
import com.example.silvercare.databinding.FragmentLoginEnterEmailBinding
import com.example.silvercare.databinding.LoginShowQrcodeBinding
import com.example.silvercare.view.activities.HomeActivity
import com.example.silvercare.viewmodel.LoginViewModel

class LoginEnterEmail : Fragment() {

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
        }
    }

}