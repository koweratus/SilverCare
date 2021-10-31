package com.example.silvercare.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.silvercare.R
import com.example.silvercare.databinding.FragmentLoginPhoneNumberBinding
import com.example.silvercare.model.Country
import com.example.silvercare.utils.*
import com.example.silvercare.utils.Utils.toast
import com.example.silvercare.utils.Utils.toastLong
import com.example.silvercare.viewmodel.LoginViewModel
import com.example.silvercare.viewmodel.SharedViewModel
import com.example.silvercare.utils.CustomProgressView
import com.example.silvercare.view.activities.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.newFixedThreadPoolContext

@AndroidEntryPoint
class LoginPhoneNumberFragment : Fragment() {

    private var country: Country? = null

    private lateinit var binding: FragmentLoginPhoneNumberBinding

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private var progressView: CustomProgressView?=null

    private val viewModel by activityViewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentLoginPhoneNumberBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressView = CustomProgressView(requireContext())
        setDataInView()
        subscribeObservers()
    }

    private fun setDataInView() {
        binding.viewmodel = viewModel
        setDefaultCountry()
        binding.txtCountryCode.setOnClickListener {
            findNavController().navigate(R.id.action_FLogin_to_FCountries)
        }
        binding.btnGetOtp.setOnClickListener {
            validate()
        }
    }

    private fun validate() {
        try {
            val mobileNo = viewModel.mobile.value?.trim()
            val country = viewModel.country.value
            when {
                mobileNo.isNullOrEmpty() -> toast(requireActivity(), "Enter mobile number")
                country == null -> toast(requireActivity(), "Select a country")
                Utils.isInvalidNo(country.code, mobileNo) -> toast(
                    requireActivity(),
                    "Enter valid mobile number"
                )
                Utils.isNoInternet(requireContext()) -> toast(requireActivity(),"No Internet Connection!")
                else -> {
                    viewModel.sendOtp(requireActivity())
                    viewModel.setProgress(true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDefaultCountry() {
        try {
            country = Utils.getDefaultCountry()
            val manager =
                requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as (TelephonyManager)?
            manager?.let {
                val countryCode = manager.networkCountryIso ?: ""
                if (countryCode.isEmpty())
                    return
                val countries = Countries.getCountries()
                for (i in countries) {
                    if (i.code.equals(countryCode, true))
                        country = i
                }
                viewModel.setCountry(country!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun subscribeObservers() {
        try {
            sharedViewModel.country.observe(viewLifecycleOwner, {
                viewModel.setCountry(it)
            })

            viewModel.getProgress().observe(viewLifecycleOwner, {
                progressView?.toggle(it)
            })

            viewModel.getVerificationId().observe(viewLifecycleOwner, { vCode ->
                vCode?.let {
                    viewModel.setProgress(false)
                    viewModel.resetTimer()
                    viewModel.setVCodeNull()
                    viewModel.setEmptyText()
                    if (findNavController().isValidDestination(R.id.LoginPhoneNumberFragment))
                        findNavController().navigate(R.id.action_FLogin_to_FVerify)
                }
            })

            viewModel.getFailed().observe(viewLifecycleOwner, {
                progressView?.dismiss()
            })

            viewModel.getTaskResult().observe(viewLifecycleOwner, { taskId ->
                if (taskId!=null && viewModel.getCredential().value?.smsCode.isNullOrEmpty()){
                    val type = viewModel.type.value
                    viewModel.fetchUser(taskId,type!!)}
            })

            viewModel.userProfileGot.observe(viewLifecycleOwner, { userId ->
                if (!userId.isNullOrEmpty() && viewModel.getCredential().value?.smsCode.isNullOrEmpty()
                    && findNavController().isValidDestination(R.id.LoginPhoneNumberFragment)) {
                    toastLong(requireContext(),"Authenticated successfully using Instant verification")
                   // val action=LoginPhoneNumberFragmentDirections.actionFLoginToFHome(userId,viewModel.lastRequestedMobile)
                   // findNavController().navigate(action)
                    findNavController().navigate(R.id.loginChooseUserTypeFragment)

                   /* startActivity(Intent(requireContext(), HomeActivity::class.java))
                    activity?.finish()*/
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        try {
            progressView?.dismissIfShowing()
            super.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}