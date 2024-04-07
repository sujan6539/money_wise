package com.sp.moneywise.ui.verification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.sp.moneywise.R
import com.sp.moneywise.databinding.ActivityVerificationBinding
import com.sp.moneywise.ui.BaseActivity
import com.sp.moneywise.ui.verification.getOTP.GetOTPFragment
import com.sp.moneywise.ui.verification.verifyOTP.VerifyOTPFragment
import com.sp.moneywise.ui.dashboard.DashboardActivity


class VerificationActivity : BaseActivity(), VerificationCallbacks {
    private lateinit var activityVerificationBinding: ActivityVerificationBinding
    private val verificationViewModel: VerificationViewModel by viewModels<VerificationViewModel>()
    private var enroll: Boolean = false
    private var email:String=""
    private var password:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVerificationBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_verification
        )
        enroll = intent.getBooleanExtra(KEY_EXTRA_ENROLL, false)

        setupGetOTPFragment()

    }

    private fun setupGetOTPFragment() {
        val getOTPFragment = GetOTPFragment.newInstance("", "")
        supportFragmentManager.beginTransaction().replace(R.id.layout_placeholder, getOTPFragment)
            .commit()
    }

    override fun reRequestOTP(phoneNumber: String) {
        verificationViewModel.reSendOTP(this, phoneNumber) { isSuccess, message ->
            if (isSuccess) {
                Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun verifyOTP(otp: String) {
        verificationViewModel.verifyOtp(otp) { isSuccess, message ->
            if (isSuccess && message?.isNotBlank() == true) {
                val intent = Intent(
                    this,
                    DashboardActivity::class.java
                )
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "The verification code entered was invalid",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    override fun onGetOTPClicked(phoneNumber: String) {
        verificationViewModel.sendOtp(this, phoneNumber) { isSuccess, message ->
            if (isSuccess && message?.isNotBlank() == true) {
                val verificationOTPFragment =
                    VerifyOTPFragment.newInstance(phoneNumber, message)
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.layout_placeholder,
                        verificationOTPFragment,
                        VerifyOTPFragment::class.simpleName
                    ).commit()
            }

        }
    }


    companion object {

        private const val KEY_EXTRA_ENROLL = "KEY_EXTRA_ENROLL"
        private const val KEY_EXTRA_EMAIL = "KEY_EXTRA_EMAIL"
        private const val KEY_EXTRA_PASSWORD = "KEY_EXTRA_PASSWORD"

        @JvmStatic
        fun newIntent(context: Context, email: String, password: String, enroll: Boolean): Intent {
            return Intent(context, VerificationActivity::class.java).apply {
                this.putExtra(KEY_EXTRA_ENROLL, enroll)
                this.putExtra(KEY_EXTRA_EMAIL, email)
                this.putExtra(KEY_EXTRA_PASSWORD, password)

            }
        }
    }


}

interface VerificationCallbacks {
    fun reRequestOTP(phoneNumber: String)

    fun verifyOTP(otp: String)

    fun onGetOTPClicked(phoneNumber: String)
}
