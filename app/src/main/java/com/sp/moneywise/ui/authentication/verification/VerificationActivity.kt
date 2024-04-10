package com.sp.moneywise.ui.authentication.verification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.sp.moneywise.R
import com.sp.moneywise.databinding.ActivityVerificationBinding
import com.sp.moneywise.longToastShow
import com.sp.moneywise.ui.authentication.BaseAuthenticationActivity
import com.sp.moneywise.ui.authentication.VerificationResultCode
import com.sp.moneywise.ui.authentication.login.LoginActivity
import com.sp.moneywise.ui.authentication.verification.getOTP.GetOTPFragment
import com.sp.moneywise.ui.authentication.verification.verifyOTP.VerifyOTPFragment
import com.sp.moneywise.ui.dashboard.DashboardActivity


class VerificationActivity : BaseAuthenticationActivity(), VerificationCallbacks {
    private lateinit var activityVerificationBinding: ActivityVerificationBinding
    private var phoneNumber: String? = null
    private var shouldEnroll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVerificationBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_verification
        )
        phoneNumber = intent.getStringExtra(KEY_PHONE_NUMBER)

        val verificationID = intent.getStringExtra(KEY_VERIFICATION_ID) ?: ""
        baseAuthenticationViewModel.verificationId = verificationID
        shouldEnroll = verificationID.isBlank()
        if (shouldEnroll) {
            setupGetOTPFragment()
        } else {
            setupVerifyFragment()
        }


    }

    private fun setupGetOTPFragment() {
        val getOTPFragment = GetOTPFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_placeholder, getOTPFragment, GetOTPFragment::class.java.simpleName)
            .commit()
    }

    private fun setupVerifyFragment() {
        val verificationOTPFragment =
            VerifyOTPFragment.newInstance(phoneNumber)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.layout_placeholder,
                verificationOTPFragment,
                VerifyOTPFragment::class.simpleName
            ).commit()
    }

    override fun reRequestOTP(phoneNumber: String) {
//        baseAuthenticationViewModel.reSendOTP(this, phoneNumber) { isSuccess, message ->
//            if (isSuccess) {
//                Toast.makeText(this, "OTP Sent", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
    }

    override fun verifyOTP(otp: String) {
        baseAuthenticationViewModel.verifyOtp(shouldEnroll, otp) { result ->
            (supportFragmentManager.findFragmentByTag(VerifyOTPFragment::class.simpleName) as? VerifyOTPFragment)?.setVisibility(
                false
            )
            result.onSuccess {
                val intent = Intent(
                    this,
                    DashboardActivity::class.java
                )
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }.onFailure {
                if (it is FirebaseAuthInvalidCredentialsException) {
                    longToastShow("Invalid SMS code")

                } else {
                    Log.e(VerificationActivity::class.java.simpleName, it.toString())
                    val mainIntent = Intent(this, LoginActivity::class.java)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(mainIntent)
                }
            }
        }
    }

    override fun onGetOTPClicked(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        baseAuthenticationViewModel.requestOtp(this, phoneNumber) { result ->
            (supportFragmentManager.findFragmentByTag(GetOTPFragment::class.java.simpleName) as? GetOTPFragment)?.setVisibility(
                false
            )
            if (result.isSuccess) {
                result.onSuccess {
                    when (it) {
                        VerificationResultCode.SUCCESS_VERIFICATION_REQUIRED -> {
                            val verificationOTPFragment =
                                VerifyOTPFragment.newInstance(phoneNumber)

                            supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.layout_placeholder,
                                    verificationOTPFragment,
                                    VerifyOTPFragment::class.simpleName
                                ).commit()
                        }

                        VerificationResultCode.SUCCESS_NO_VERIFICATION_REQUIRED -> {
                            val intent = Intent(
                                this,
                                DashboardActivity::class.java
                            )
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }

                        VerificationResultCode.SUCCESS_MFA_DISABLED -> {
                            setupVerifyFragment()

                        }
                    }
                }
            } else {
                result.onFailure {
                    if (it is UnverifiedEmailException) {
                        longToastShow("Email Unverified. Please verify email")
                        val mainIntent = Intent(this, LoginActivity::class.java)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(mainIntent)
                        finish()

                    } else {
                        longToastShow("Session Expired. Logging out!")
                        val mainIntent = Intent(this, LoginActivity::class.java)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(mainIntent)
                        finish()
                    }
                }
            }
        }
    }


    companion object {

        private const val KEY_PHONE_NUMBER = "KEY_PHONE_NUMBER"
        private const val KEY_VERIFICATION_ID = "KEY_VERIFICATION_ID"

        @JvmStatic
        fun newIntent(context: Context, verificationID: String, phoneNumber: String?): Intent {
            val i = Intent(context, VerificationActivity::class.java)
            i.putExtra(KEY_PHONE_NUMBER, phoneNumber ?: "")
            i.putExtra(KEY_VERIFICATION_ID, verificationID)
            return i

        }
    }


}

interface VerificationCallbacks {
    fun reRequestOTP(phoneNumber: String)

    fun verifyOTP(otp: String)

    fun onGetOTPClicked(phoneNumber: String)
}
