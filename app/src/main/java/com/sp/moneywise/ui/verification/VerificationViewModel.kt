package com.sp.moneywise.ui.verification
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
import com.google.firebase.auth.PhoneMultiFactorAssertion
import java.util.concurrent.TimeUnit


class VerificationViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var verificationId: String? = null

    fun sendOtp(
        activity: Activity,
        phoneNumber: String,
        callback: (Boolean, String?) -> Unit
    ) {
        //verify phone number
        val options = PhoneAuthOptions.newBuilder().setPhoneNumber("+977$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(activity)
            .setCallbacks(object : OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(
                        phoneAuthCredential,
                        verificationId!!,
                        callback
                    )
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in response to invalid requests for
                    // verification, like an incorrect phone number.
                    if (e is FirebaseAuthInvalidCredentialsException) {
                        Log.d(VerificationViewModel::class.simpleName, e.toString())
                    } else if (e is FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        Log.d(VerificationViewModel::class.simpleName, e.toString())
                    }
                    callback.invoke(false, e.message)

                }

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: ForceResendingToken,
                ) {
                    super.onCodeSent(verificationId, forceResendingToken)
                    this@VerificationViewModel.verificationId = verificationId
                    callback.invoke(true, verificationId)

                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    fun verifyOtp(otp: String, callback: (Boolean, String?) -> Unit) {
        verificationId?.let { verificationId ->
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            signInWithPhoneAuthCredential(credential, verificationId, callback)
        } ?: Log.e(VerificationViewModel::class.simpleName, "verification id is wrong")
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        verificationId: String,
        callback: (Boolean, String?) -> Unit,
    ) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                enrollSecondFactor(verificationId, credential.smsCode ?: "", callback)
                callback.invoke(true, verificationId)
            } else {
                var message: String? = task.exception?.message
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    message = "Invalid OTP"
                } else if (task.exception is FirebaseTooManyRequestsException) {
                    message = "SMS quota exceeded"
                }
                callback.invoke(false, message)
            }
        }
    }

    private fun enrollSecondFactor(
        verificationId: String?,
        otp: String,
        callback: (Boolean, String?) -> Unit,
    ) {
        verificationId?.let {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            firebaseAuth.currentUser?.let { user ->
                user.multiFactor.session
                    .addOnSuccessListener {
                        val phoneMultiFactorAssertion =
                            PhoneMultiFactorAssertion(credential)
                        user.multiFactor
                            .enroll(phoneMultiFactorAssertion, "My personal phone number")
                            .addOnSuccessListener {
                                callback.invoke(true, null)
                            }
                            .addOnFailureListener { e ->
                                handleError(e, callback)
                            }
                    }
                    .addOnFailureListener { e ->
                        handleError(e, callback)
                    }
            } ?: run {
                callback.invoke(false, "User not authenticated")
            }
        } ?: run {
            callback.invoke(false, "Verification ID is null")
        }
    }


    private fun handleError(exception: Exception, callback: (Boolean, String?) -> Unit) {
        val errorMessage = when (exception) {
            is FirebaseAuthInvalidCredentialsException -> "Invalid OTP"
            is FirebaseException -> exception.message
            else -> "Unknown error occurred"
        }
        callback.invoke(false, errorMessage)
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
