package com.sp.moneywise.ui.authentication.verification.verifyOTP

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.sp.moneywise.R
import com.sp.moneywise.databinding.FragmentVerifyOtpBinding
import com.sp.moneywise.ui.authentication.verification.VerificationCallbacks

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PHONE_NUMBER = "ARG_PHONE_NUMBER"
private const val ARG_VERIFICATION_ID = "ARG_VERIFICATION_ID"

/**
 * A simple [Fragment] subclass.
 * Use the [VerifyOTPFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VerifyOTPFragment : Fragment() {

    private var phoneNumber: String? = null
    private var verificationId: String? = null
    private lateinit var fragmentVerifyOtpBinding: FragmentVerifyOtpBinding
    private var verificationCallbacks: VerificationCallbacks? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            phoneNumber = it.getString(ARG_PHONE_NUMBER)
            verificationId = it.getString(ARG_VERIFICATION_ID)
        }
        verificationCallbacks = activity as? VerificationCallbacks
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        fragmentVerifyOtpBinding = DataBindingUtil.inflate<FragmentVerifyOtpBinding>(
            inflater,
            R.layout.fragment_verify_otp,
            container,
            false
        )

        setTextMobile()
        setupOTPInputs()
        setListener()

        return fragmentVerifyOtpBinding.root
    }

    private fun setListener() {
        fragmentVerifyOtpBinding.buttonVerify.setOnClickListener {
            if (fragmentVerifyOtpBinding.inputCode1.text.toString().trim().isEmpty()
                || fragmentVerifyOtpBinding.inputCode2.text.toString().trim().isEmpty()
                || fragmentVerifyOtpBinding.inputCode3.text.toString().trim().isEmpty()
                || fragmentVerifyOtpBinding.inputCode4.text.toString().trim().isEmpty()
                || fragmentVerifyOtpBinding.inputCode5.text.toString().trim().isEmpty()
                || fragmentVerifyOtpBinding.inputCode6.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    activity,
                    "Please enter valid code",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }
            val code: String = fragmentVerifyOtpBinding.inputCode1.text.toString() +
                    fragmentVerifyOtpBinding.inputCode2.text.toString() +
                    fragmentVerifyOtpBinding.inputCode3.text.toString() +
                    fragmentVerifyOtpBinding.inputCode4.text.toString() +
                    fragmentVerifyOtpBinding.inputCode5.text.toString() +
                    fragmentVerifyOtpBinding.inputCode6.text.toString()

                fragmentVerifyOtpBinding.progressBar.visibility = View.VISIBLE
                fragmentVerifyOtpBinding.buttonVerify.visibility = View.INVISIBLE
                verificationCallbacks?.verifyOTP(code)
        }
        fragmentVerifyOtpBinding.textResendOTP.setOnClickListener {
            phoneNumber?.let { verificationCallbacks?.reRequestOTP(it) } ?: Toast.makeText(
                activity,
                "Invalid Phone number",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun setVisibility(show: Boolean){
        if(show){
            fragmentVerifyOtpBinding.progressBar.visibility = View.VISIBLE
            fragmentVerifyOtpBinding.buttonVerify.visibility = View.INVISIBLE
        }else{
            fragmentVerifyOtpBinding.progressBar.visibility = View.INVISIBLE
            fragmentVerifyOtpBinding.buttonVerify.visibility = View.VISIBLE

        }
    }

    /** If Intent() getStringExtra == "mobile" -> startActivity(VerifyActivity),
     * (TextView) textMobile will be received value "user mobile number" */
    private fun setTextMobile() {
        fragmentVerifyOtpBinding.textMobile.text = String.format(
            "+977-%s", activity?.intent?.getStringExtra("mobile")
        )
    }

    /** When the edittext1 (inputCode1) was inserted, the cursor will be jump to the
     * next edittext (in this case it would be "inputCode2") */
    private fun setupOTPInputs() {
        fragmentVerifyOtpBinding.inputCode1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    fragmentVerifyOtpBinding.inputCode2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        fragmentVerifyOtpBinding.inputCode2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    fragmentVerifyOtpBinding.inputCode3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        fragmentVerifyOtpBinding.inputCode3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    fragmentVerifyOtpBinding.inputCode4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        fragmentVerifyOtpBinding.inputCode4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    fragmentVerifyOtpBinding.inputCode5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        fragmentVerifyOtpBinding.inputCode5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    fragmentVerifyOtpBinding.inputCode6.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment VerifyOTPFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance( phoneNumber: String?) =
            VerifyOTPFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PHONE_NUMBER, phoneNumber)
                }
            }
    }
}