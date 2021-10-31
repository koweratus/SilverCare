package com.example.silvercare.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.silvercare.R
import com.example.silvercare.databinding.FragmentLoginChooseUserTypeBinding
import com.example.silvercare.databinding.FragmentLoginPersonalInformationBinding
import com.example.silvercare.viewmodel.LoginViewModel

class LoginChooseUserTypeFragment : Fragment() {
    private lateinit var binding: FragmentLoginChooseUserTypeBinding
    private val viewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginChooseUserTypeBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        binding.btnFinish.setOnClickListener{

                viewModel.setUserType(binding.radioCaretaker.isChecked)
                findNavController().navigate(R.id.action_loginChooseUserTypeFragment_to_LoginPhoneNumberFragment)

        }


    }

}