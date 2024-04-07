package com.sp.moneywise.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class RegisterViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

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
            signInWithEmailAndPassword(displayName, email.trim(), callback)
        }.addOnFailureListener {
            callback.invoke(false, it.toString())
        }

    }

    private fun signInWithEmailAndPassword(
        displayName: String,
        email: String,
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
}
