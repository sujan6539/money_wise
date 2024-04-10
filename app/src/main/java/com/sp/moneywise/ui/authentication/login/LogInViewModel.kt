package com.sp.moneywise.ui.authentication.login

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorInfo
import com.google.firebase.auth.UserProfileChangeRequest
import com.sp.moneywise.ui.authentication.VerificationResultCode
import java.util.concurrent.TimeUnit


class LogInViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    var credential: PhoneAuthCredential? = null
    var verificationId: String? = null
    var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    fun getPhoneNumber(): String? {
        return firebaseAuth.currentUser?.phoneNumber
    }

    fun registerUser(
        displayName: String,
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit,
    ) {
        firebaseAuth.createUserWithEmailAndPassword(
            email.trim(),
            password.trim()
        ).addOnSuccessListener {
            // Step 2: Sign In the User
            signInWithEmailAndPassword(displayName, callback)
        }.addOnFailureListener {
            callback.invoke(false, it.toString())
        }

    }

    private fun signInWithEmailAndPassword(
        displayName: String,
        callback: (Boolean, String?) -> Unit,
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        // Check if email is already verified
        if (!currentUser.isEmailVerified) {
            // Send verification email
            currentUser.sendEmailVerification()
                .addOnSuccessListener {
                    callback.invoke(true, "Verification email sent")
                }
                .addOnFailureListener { e ->
                    callback.invoke(false, e.toString())
                }
        } else {
            callback.invoke(true, "email is verified")
        }

        val profileUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName.trim())
            .build()
        currentUser.updateProfile(profileUpdate).addOnSuccessListener {
            callback.invoke(true, currentUser.toString())
        }.addOnFailureListener {
            callback.invoke(false, it.toString())
        }
    }

    fun loginWithEmailAndPassword(
        activity: Activity,
        email: String,
        password: String,
        callback: (Result<VerificationResultCode>) -> Unit,
    ) {
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User is not enrolled with a second factor and is successfully
                    // signed in.
                    // ...
                    callback.invoke(Result.success(VerificationResultCode.SUCCESS_MFA_DISABLED))
                } else if (task.exception is FirebaseAuthMultiFactorException) {

                    val callbacks =
                        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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
                                this@LogInViewModel.credential = credential
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
                            }

                            override fun onCodeSent(
                                verificationId: String,
                                forceResendingToken: PhoneAuthProvider.ForceResendingToken,
                            ) {
                                // The SMS verification code has been sent to the provided phone number.
                                // We now need to ask the user to enter the code and then construct a
                                // credential by combining the code with a verification ID.
                                // Save the verification ID and resending token for later use.
                                this@LogInViewModel.verificationId = verificationId
                                this@LogInViewModel.forceResendingToken = forceResendingToken
                                callback(Result.success(VerificationResultCode.SUCCESS_VERIFICATION_REQUIRED))
                            }
                        }


                    // The user is a multi-factor user. Second factor challenge is
                    // required.

                    val multiFactorResolver =
                        (task.exception as FirebaseAuthMultiFactorException).resolver
                    val phoneAuthOptions = PhoneAuthOptions.newBuilder()
                        .setMultiFactorHint(multiFactorResolver.hints[0] as PhoneMultiFactorInfo)
                        .setActivity(activity)
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .setMultiFactorSession(multiFactorResolver.session)
                        .setCallbacks(callbacks) // Optionally disable instant verification.
                        .build()
                    // Send SMS verification code
                    PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
                } else {
                    callback.invoke(Result.failure(FirebaseException("Wrong Password")))
                }
            }


    }

}
