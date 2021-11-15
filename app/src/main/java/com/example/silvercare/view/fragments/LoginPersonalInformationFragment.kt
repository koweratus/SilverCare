package com.example.silvercare.view.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.silvercare.R
import com.example.silvercare.databinding.FragmentLoginPersonalInformationBinding
import com.example.silvercare.utils.BaseActivity
import com.example.silvercare.utils.CustomProgressView
import com.example.silvercare.utils.dismissIfShowing
import com.example.silvercare.view.activities.MainActivity
import com.example.silvercare.viewmodel.LoginViewModel
import java.time.LocalDate.now
import java.time.LocalDateTime.now
import java.time.Year
import java.util.*

class LoginPersonalInformationFragment : BaseActivity() {

    private lateinit var binding: FragmentLoginPersonalInformationBinding
    private val viewModel by activityViewModels<LoginViewModel>()


    val c = Calendar.getInstance()
    val cMin = Calendar.getInstance()
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


        /*c.add(Calendar.YEAR,-18)
        c.getActualMaximum(Calendar.DAY_OF_MONTH)
        c.getActualMaximum(Calendar.MONTH)*/
        val currentYear = c.get(Calendar.YEAR) - 18
<<<<<<< HEAD
        c.set(currentYear, 11, 31)
        cMin.set(1929, 11, 31)
        binding.viewmodel = viewModel

        binding.etDatePickerSenior.setOnClickListener {
            selectDateForSenior()
        }

        binding.etDatePickerSenior.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                selectDateForSenior()
            }
        }

        binding.etDatePickerCaretaker.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                selectDateForCaretaker()
            }
        }
        binding.etDatePickerCaretaker.setOnClickListener {
            selectDateForCaretaker()
        }

=======
        c.set(currentYear ,11,31)
        cMin.set(1929,11,31)
        binding.viewmodel = viewModel
        binding.etDatePickerSenior.setOnClickListener{
            selectDateForSenior()
        }
        binding.etDatePickerCaretaker.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                selectDateForCaretaker()
            }
        }
        binding.etDatePickerCaretaker.setOnClickListener{
            selectDateForCaretaker()
        }
        binding.etDatePickerSenior.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                selectDateForSenior()
            }
        }
>>>>>>> 5995829ebe2a1f3b8a2c32684b52e223815f3812
        binding.btnFinish.setOnClickListener {
            if (validatePersonalInformationDetails()) {
                findNavController().navigate(R.id.loginShowQrCode)
                subscribeObservers()

            } else {
                validatePersonalInformationDetails()
<<<<<<< HEAD
                //  showErrorSnackBar(resources.getString(R.string.error_personal_information), true,requireActivity())
=======
              //  showErrorSnackBar(resources.getString(R.string.error_personal_information), true,requireActivity())
>>>>>>> 5995829ebe2a1f3b8a2c32684b52e223815f3812

            }
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
            requireContext(), R.style.customDatePickerStyle, { datePicker, yy, mm, dd ->
                var dtS = "$dd/${mm + 1}/$yy"
                binding.etDatePickerSenior.setText(dtS)
            },
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = c.timeInMillis
<<<<<<< HEAD
            datePicker.minDate = cMin.timeInMillis
        }.show()
=======
        datePicker.minDate = cMin.timeInMillis }.show()
>>>>>>> 5995829ebe2a1f3b8a2c32684b52e223815f3812

    }

    private fun selectDateForCaretaker() {
        DatePickerDialog(
<<<<<<< HEAD
            requireContext(), R.style.customDatePickerStyle,
            { datePicker, yy, mm, dd ->
=======
            requireContext(), R.style.customDatePickerStyle, { datePicker, yy, mm, dd ->
>>>>>>> 5995829ebe2a1f3b8a2c32684b52e223815f3812
                var dtC = "$dd/${mm + 1}/$yy"
                binding.etDatePickerCaretaker.setText(dtC)
            },
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH),

<<<<<<< HEAD
            ).apply {
            datePicker.maxDate = c.timeInMillis
            datePicker.minDate = cMin.timeInMillis
        }.show()
=======
        ).apply {
            datePicker.maxDate = c.timeInMillis
            datePicker.minDate = cMin.timeInMillis }.show()
>>>>>>> 5995829ebe2a1f3b8a2c32684b52e223815f3812

    }

    private fun validatePersonalInformationDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etProfileName.text.toString().trim { it <= ' ' }) -> {
<<<<<<< HEAD
                showErrorSnackBar(
                    resources.getString(R.string.error_caretaker_name),
                    true,
                    requireActivity()
                )
                false
            }
            TextUtils.isEmpty(binding.etDatePickerCaretaker.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.error_caretaker_dob),
                    true,
                    requireActivity()
                )
=======
                showErrorSnackBar(resources.getString(R.string.error_caretaker_name), true,requireActivity())
                false
            }
            TextUtils.isEmpty(binding.etDatePickerCaretaker.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.error_caretaker_dob), true,requireActivity())
>>>>>>> 5995829ebe2a1f3b8a2c32684b52e223815f3812
                false
            }

            TextUtils.isEmpty(binding.etSeniorName.text.toString().trim { it <= ' ' }) -> {
<<<<<<< HEAD
                showErrorSnackBar(
                    resources.getString(R.string.error_senior_name),
                    true,
                    requireActivity()
                )
=======
                showErrorSnackBar(resources.getString(R.string.error_senior_name), true,requireActivity())
>>>>>>> 5995829ebe2a1f3b8a2c32684b52e223815f3812
                false
            }

            TextUtils.isEmpty(binding.etDatePickerSenior.text.toString().trim { it <= ' ' }) -> {
<<<<<<< HEAD
                showErrorSnackBar(
                    resources.getString(R.string.error_senior_dob),
                    true,
                    requireActivity()
                )
=======
                showErrorSnackBar(resources.getString(R.string.error_senior_dob), true,requireActivity())
>>>>>>> 5995829ebe2a1f3b8a2c32684b52e223815f3812
                false
            }

            else -> {
                true
            }
        }
    }


}

