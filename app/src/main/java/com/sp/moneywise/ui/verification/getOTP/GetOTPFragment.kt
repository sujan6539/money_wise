package com.sp.moneywise.ui.verification.getOTP

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sp.moneywise.R
import com.sp.moneywise.databinding.FragmentGetOtpBinding
import com.sp.moneywise.ui.verification.VerificationCallbacks


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GetOTPFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GetOTPFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var shouldEnroll: Boolean = false
    private var param2: String? = null
    private lateinit var fragmentGetOtpBinding: FragmentGetOtpBinding
    private var verificationCallbacks: VerificationCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            shouldEnroll = it.getBoolean(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
        verificationCallbacks = activity as? VerificationCallbacks
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        fragmentGetOtpBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_get_otp, container, false)
        setupListener()
        return fragmentGetOtpBinding.root
    }

    private fun setupListener() {
        fragmentGetOtpBinding.buttonGetOTP.setOnClickListener { v ->
            //toast error
            if (fragmentGetOtpBinding.inputMobile.text.toString().isEmpty()) {
                Toast.makeText(activity, "Enter mobile", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //set visibility
            fragmentGetOtpBinding.buttonGetOTP.visibility = View.INVISIBLE
            fragmentGetOtpBinding.progressBar.visibility = View.VISIBLE
            //verify phone number
            (activity as? VerificationCallbacks)?.onGetOTPClicked(fragmentGetOtpBinding.inputMobile.text.toString())

        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GetOTPFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GetOTPFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}