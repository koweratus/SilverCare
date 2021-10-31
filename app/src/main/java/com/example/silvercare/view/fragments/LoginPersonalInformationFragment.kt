package com.example.silvercare.view.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.silvercare.R
import com.example.silvercare.databinding.FragmentLoginPersonalInformationBinding
import com.example.silvercare.utils.CustomProgressView
import com.example.silvercare.utils.dismissIfShowing
import com.example.silvercare.viewmodel.LoginViewModel
import java.util.*

class LoginPersonalInformationFragment : Fragment() {

    private lateinit var binding: FragmentLoginPersonalInformationBinding
    private val viewModel by activityViewModels<LoginViewModel>()

    val c = Calendar.getInstance()
    var year = c[Calendar.YEAR]
    var month = c[Calendar.MONTH]
    var day = c[Calendar.DAY_OF_MONTH]
    private lateinit var datePickerDialog: DatePickerDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginPersonalInformationBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel

        binding.etDatePickerSenior.setOnClickListener {
            selectDateForSenior()
        }
        binding.etDatePickerCaretaker.setOnClickListener {
            selectDateForCaretaker()
        }
        binding.btnFinish.setOnClickListener{
            subscribeObservers()
            findNavController().navigate(R.id.loginShowQrCode)
        }
    }
    private fun subscribeObservers() {
        viewModel.setSeniorName(binding.etSeniorName.text.toString())
        viewModel.setCaretakerName(binding.etProfileName.text.toString())
        viewModel.setSeniorDob(binding.etDatePickerSenior.text.toString())
        viewModel.getCurrentUserID()
    }
    private fun selectDateForSenior() {
        DatePickerDialog(
            requireContext(), DatePickerDialog.OnDateSetListener { datePicker, yy, mm, dd ->
                var dtS = "$dd/${mm + 1}/$yy"
                binding.etDatePickerSenior.setText(dtS)
            },
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        ).show()

    }
    private fun selectDateForCaretaker() {
        DatePickerDialog(
            requireContext(), DatePickerDialog.OnDateSetListener { datePicker, yy, mm, dd ->
                var dtC = "$dd/${mm + 1}/$yy"
                binding.etDatePickerCaretaker.setText(dtC)

            },
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        ).show()

    }
}

