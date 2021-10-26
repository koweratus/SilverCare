package com.example.silvercare.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.silvercare.databinding.FragmentLoginFinishProfileBinding
import com.example.silvercare.databinding.FragmentLoginPhoneNumberBinding

class LoginFinishProfileFragment : Fragment() {

    private var _binding: FragmentLoginFinishProfileBinding? = null
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // _binding = FragmentLoginFinishProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}