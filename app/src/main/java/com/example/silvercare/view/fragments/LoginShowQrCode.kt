package com.example.silvercare.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.silvercare.R
import com.example.silvercare.databinding.LoginShowQrcodeBinding
import com.example.silvercare.utils.BaseActivity
import com.example.silvercare.viewmodel.LoginViewModel
import com.google.zxing.WriterException

class LoginShowQrCode : BaseActivity() {

    private lateinit var binding: LoginShowQrcodeBinding
    private val viewModel by activityViewModels<LoginViewModel>()


    var bitmap: Bitmap? = null
    var qrgEncoder: QRGEncoder? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LoginShowQrcodeBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
        // initializing onclick listener for button.

    }

    @SuppressLint("ServiceCast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        getQrCode()
        binding.btnContinueToEmail.setOnClickListener{
            viewModel.getTaskResult().observe(viewLifecycleOwner, { taskId ->
                taskId?.let {
                   viewModel.checkIfUsersAreConnected(taskId)
                }
            })
            viewModel.isProfileCompleted.observe(viewLifecycleOwner, {
                if (viewModel.isProfileCompleted.value == true){
                    findNavController().navigate(R.id.loginEnterEmail)
                    showErrorSnackBar(resources.getString(R.string.success_qr_code),false,requireActivity())
                }else{
                    showErrorSnackBar(resources.getString(R.string.error_qr_code),true,requireActivity())
                }
            })


        }
    }

    fun getQrCode() {

        val caretakerName = viewModel.seniorName.value.toString()
        val seniorName = viewModel.seniorName.value.toString()
        val seniorDob = viewModel.seniorDob.value.toString()
        val caretakerDob = viewModel.seniorDob.value.toString()
        val id = viewModel.getCurrentUserID()
        val mobile = viewModel.mobile.value.toString()
        val msg = "$seniorName;$seniorDob;$id;$mobile;$caretakerName;$caretakerDob"
        if (TextUtils.isEmpty(seniorName)) {

            // if the edittext inputs are empty then execute
            // this method showing a toast message.
            Toast.makeText(
                requireContext(),
                "Enter some text to generate QR Code",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // below line is for getting
            // the windowmanager service.
            val manager =
                activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            // initializing a variable for default display.
            val display = manager.defaultDisplay

            // creating a variable for point which
            // is to be displayed in QR Code.
            val point = Point()
            display.getSize(point)

            // getting width and
            // height of a point
            val width = point.x
            val height = point.y

            // generating dimension from width and height.
            var dimen = if (width < height) width else height
            dimen = dimen * 3 / 4

            // setting this dimensions inside our qr code
            // encoder to generate our qr code.
            qrgEncoder =
                QRGEncoder(msg, null, QRGContents.Type.TEXT, dimen)
            try {
                // getting our qrcode in the form of bitmap.
                bitmap = qrgEncoder!!.encodeAsBitmap()
                // the bitmap is set inside our image
                // view using .setimagebitmap method.
                binding.idIVQrcode.setImageBitmap(bitmap)
            } catch (e: WriterException) {
                // this method is called for
                // exception handling.
                Log.e("Tag", e.toString())
            }
        }


    }


}