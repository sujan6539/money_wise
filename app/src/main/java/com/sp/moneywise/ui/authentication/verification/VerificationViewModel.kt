package com.sp.moneywise.ui.authentication.verification

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.PhoneMultiFactorGenerator
import com.sp.moneywise.ui.authentication.VerificationResultCode
import java.util.concurrent.TimeUnit


class VerificationViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var verificationId: String? = null
    private var phoneAuthCredential: PhoneAuthCredential? = null
    private var forceResendingToken: ForceResendingToken? = null
    private var phoneNumber: String? = null

    fun sendOtp(
        activity: Activity,
        phoneNumber: String,
        callback: (Result<VerificationResultCode>) -> Unit,
    ) {
        // Check if email is already verified
        if (firebaseAuth.currentUser?.isEmailVerified == false) {
            callback(Result.failure(UnverifiedEmailException()))
            return
        }

        val callbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1) Instant verification. In some cases, the phone number can be
                //    instantly verified without needing to send or enter a verification
                //    code. You can disable this feature by calling
                //    PhoneAuthOptions.builder#requireSmsValidation(true) when building
                //    the options to pass to PhoneAuthProvider#verifyPhoneNumber().
                // 2) Auto-retrieval. On some devices, Google Play services can
                //    automatically detect the incoming verification SMS and perform
                //    verification without user action.
                this@VerificationViewModel.phoneAuthCredential = credential
                callback(Result.success(VerificationResultCode.SUCCESS_NO_VERIFICATION_REQUIRED))
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in response to invalid requests for
                // verification, like an incorrect phone number.
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
                callback(Result.failure(e))
                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                verificationId: String, forceResendingToken: ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number.
                // We now need to ask the user to enter the code and then construct a
                // credential by combining the code with a verification ID.
                // Save the verification ID and resending token for later use.
                this@VerificationViewModel.verificationId = verificationId
                this@VerificationViewModel.forceResendingToken = forceResendingToken
                callback(Result.success(VerificationResultCode.SUCCESS_VERIFICATION_REQUIRED))
            }
        }


        firebaseAuth.currentUser?.multiFactor?.session?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.run{
                    val phoneAuthOptions = PhoneAuthOptions.newBuilder()
                        .setPhoneNumber("+977$phoneNumber")
                        .setActivity(activity)
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .setMultiFactorSession(this)
                        .setCallbacks(callbacks)
                        .build()

                    PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
                }
            }
        }


    }


    fun verifyOtp(verificationId:String, otp: String, callback:  (Result<VerificationResultCode>) -> Unit) =
        verificationId.let { verificationId ->
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            signInWithPhoneAuthCredential(credential, callback)
        }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        callback: (Result<VerificationResultCode>) -> Unit,
    ) {
        val multiFactorAssertion
                = PhoneMultiFactorGenerator.getAssertion(credential)

        FirebaseAuth.getInstance()
            .currentUser
            ?.multiFactor
            ?.enroll(multiFactorAssertion, "+977$phoneNumber")
            ?.addOnCompleteListener {
                Log.e("HELLO", "onComplete")
                callback(Result.success(VerificationResultCode.SUCCESS_NO_VERIFICATION_REQUIRED))
                // ...
            }?.addOnFailureListener {
                Log.e("HELLO", "onFailure")
                callback(Result.failure(it))
            }
    }


    fun reSendOTP(activity: Activity, phoneNumber: String, callback: (Boolean, String?) -> Unit) {
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber("+977$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {}
                override fun onVerificationFailed(e: FirebaseException) {
                    callback.invoke(false, e.message)
                }

                override fun onCodeSent(
                    newVerificationId: String,
                    forceResendingToken: ForceResendingToken,
                ) {
                    verificationId = newVerificationId
                    callback.invoke(true, verificationId)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}

class UnverifiedEmailException():Exception()
