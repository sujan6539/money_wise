package com.sp.moneywise.ui.authentication

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
import com.google.firebase.auth.PhoneMultiFactorGenerator
import com.google.firebase.auth.PhoneMultiFactorInfo
import com.google.firebase.auth.UserProfileChangeRequest
import java.util.concurrent.TimeUnit

class BaseAuthenticationViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    var credential: PhoneAuthCredential? = null
    var verificationId: String? = null
    var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    var currentUser = firebaseAuth.currentUser

    var emailVerified = currentUser?.isEmailVerified

    fun getPhoneNumber(): String? {
        return currentUser?.phoneNumber
    }

    fun registerUser(
        displayName: String,
        email: String,
        password: String,
        callback: (Result<String>) -> Unit,
    ) {
        firebaseAuth.createUserWithEmailAndPassword(
            email.trim(),
            password.trim()
        ).addOnSuccessListener {
            val currentUser = firebaseAuth.currentUser!!
            // Step 2: Send verification email if email is not verified
            if (!currentUser.isEmailVerified) {
                // Send verification email
                currentUser.sendEmailVerification()
            }
            // Step 3: Update the profile with user name
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName.trim())
                .build()
            currentUser.updateProfile(profileUpdate).addOnSuccessListener {
                callback.invoke(Result.success(currentUser.toString()))
            }.addOnFailureListener {
                callback.invoke(Result.failure(it.fillInStackTrace()))
            }
        }.addOnFailureListener {
            callback.invoke(Result.failure(it.fillInStackTrace()))
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
                currentUser = firebaseAuth.currentUser
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
                                this@BaseAuthenticationViewModel.credential = credential
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
                                this@BaseAuthenticationViewModel.verificationId = verificationId
                                this@BaseAuthenticationViewModel.forceResendingToken =
                                    forceResendingToken
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

    fun requestOtp(
        activity: Activity,
        phoneNumber: String,
        callback: (Result<VerificationResultCode>) -> Unit,
    ) {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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
                this@BaseAuthenticationViewModel.credential = credential
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
                verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number.
                // We now need to ask the user to enter the code and then construct a
                // credential by combining the code with a verification ID.
                // Save the verification ID and resending token for later use.
                this@BaseAuthenticationViewModel.verificationId = verificationId
                this@BaseAuthenticationViewModel.forceResendingToken = forceResendingToken
                callback(Result.success(VerificationResultCode.SUCCESS_VERIFICATION_REQUIRED))
            }
        }

        currentUser = firebaseAuth.currentUser
        currentUser?.multiFactor?.session?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.run {
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

    fun verifyOtp(
        shouldEnroll: Boolean,
        otp: String,
        callback: (Result<VerificationResultCode>) -> Unit,
    ) {
        val credential = PhoneAuthProvider.getCredential(this.verificationId ?: "", otp)

        if (shouldEnroll) {
            enrollToMFA(credential, callback)
        } else {
            signInWithPhoneAuthCredential(credential, callback)
        }
    }

    private fun enrollToMFA(
        credential: PhoneAuthCredential,
        callback: (Result<VerificationResultCode>) -> Unit,
    ) {
        val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
        FirebaseAuth.getInstance()
            .currentUser
            ?.multiFactor
            ?.enroll(multiFactorAssertion, "my phone number")
            ?.addOnCompleteListener {
                callback(Result.success(VerificationResultCode.SUCCESS_NO_VERIFICATION_REQUIRED))
                // ...
            }?.addOnFailureListener {
                callback(Result.failure(it))
            }
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        callback: (Result<VerificationResultCode>) -> Unit,
    ) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)

            if (task.isSuccessful) {
                callback.invoke(Result.success(VerificationResultCode.SUCCESS_NO_VERIFICATION_REQUIRED))
            } else {
                if (task.exception is FirebaseAuthMultiFactorException) {
                    // The user is a multi-factor user. Second factor challenge is
                    // required.
                    val multiFactorResolver =
                        (task.exception as FirebaseAuthMultiFactorException).resolver
                    // ...
                    multiFactorResolver
                        .resolveSignIn(multiFactorAssertion)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                val authResult = task1.result
                                // AuthResult will also contain the user, additionalUserInfo,
                                // and an optional credential (null for email/password)
                                // associated with the first factor sign-in.

                                // For example, if the user signed in with Google as a first
                                // factor, authResult.getAdditionalUserInfo() will contain data
                                // related to Google provider that the user signed in with;
                                // authResult.getCredential() will contain the Google OAuth
                                //   credential;
                                // authResult.getCredential().getAccessToken() will contain the
                                //   Google OAuth access token;
                                // authResult.getCredential().getIdToken() contains the Google
                                //   OAuth ID token.

                                callback.invoke(Result.success(VerificationResultCode.SUCCESS_NO_VERIFICATION_REQUIRED))
                            }
                        }
                }else if(task.exception is FirebaseAuthInvalidCredentialsException){
                    callback.invoke(Result.failure(task.exception!!))
                }else{
                    callback.invoke(Result.failure(task.exception!!))
                }
            }
        }

    }

}

enum class VerificationResultCode {
    SUCCESS_MFA_DISABLED,
    SUCCESS_VERIFICATION_REQUIRED,
    SUCCESS_NO_VERIFICATION_REQUIRED,
}