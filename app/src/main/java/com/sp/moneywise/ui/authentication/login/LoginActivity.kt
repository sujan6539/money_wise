package com.sp.moneywise.ui.authentication.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.sp.moneywise.BuildConfig
import com.sp.moneywise.R
import com.sp.moneywise.isConnected
import com.sp.moneywise.longToastShow
import com.sp.moneywise.ui.authentication.BaseAuthenticationActivity
import com.sp.moneywise.ui.authentication.VerificationResultCode
import com.sp.moneywise.ui.authentication.verification.VerificationActivity
import com.sp.moneywise.ui.dashboard.DashboardActivity
import com.sp.moneywise.validateEmail
import com.sp.moneywise.validatePassword

class LoginActivity : BaseAuthenticationActivity() {
    private val googleSignInRequestCode = 234
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loadingDialog = Dialog(this, R.style.DialogCustomTheme).apply {
            setContentView(R.layout.loading_dialog)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
        }
        val signUpABtn = findViewById<Button>(R.id.signUpABtn)
        signUpABtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


        val edEmail = findViewById<TextInputEditText>(R.id.edEmail)
        val edEmailL = findViewById<TextInputLayout>(R.id.edEmailL)

        edEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEmail(edEmail, edEmailL)
            }

        })


        val edPassword = findViewById<TextInputEditText>(R.id.edPassword)
        val edPasswordL = findViewById<TextInputLayout>(R.id.edPasswordL)

        edPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validatePassword(edPassword, edPasswordL)
            }

        })


        val signInBtn = findViewById<Button>(R.id.signInBtn)
        signInBtn.setOnClickListener {
            if (validateEmail(edEmail, edEmailL)
                && validatePassword(edPassword, edPasswordL)
            ) {
                if (isConnected(this)) {
                    loadingDialog?.show()

                    if (baseAuthenticationViewModel.emailVerified == false) {
                        loadingDialog?.dismiss()
                        longToastShow("Email not verified")
                    } else {
                        val email = edEmail.text.toString()
                        val password = edPassword.text.toString()
                        startLoginProcess(email, password)
                    }

                } else {
                    longToastShow("No Internet Connection!")
                }
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.FIREBASE_CLIENT_ID)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleSignInBtn = findViewById<SignInButton>(R.id.googleSignInBtn)
        googleSignInBtn.setOnClickListener {
            if (isConnected(this)) {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, googleSignInRequestCode)
            } else {
                longToastShow("No Internet Connection!")
            }
        }


        val forgotPasswordTxt = findViewById<TextView>(R.id.forgotPasswordTxt)
        forgotPasswordTxt.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

    }

    private fun startLoginProcess(email: String, password: String) {
        baseAuthenticationViewModel.loginWithEmailAndPassword(
            this, email, password
        ) { result ->
            if (result.isSuccess) {
                result.onSuccess {
                    when (it) {
                        VerificationResultCode.SUCCESS_VERIFICATION_REQUIRED -> {
                            loadingDialog?.dismiss()
                            val mainIntent = VerificationActivity.newIntent(
                                this,
                                baseAuthenticationViewModel.getPhoneNumber()
                            )
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(mainIntent)
                            finish()
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
                            val mainIntent =
                                VerificationActivity.newIntent(
                                    this,
                                    phoneNumber = null
                                )
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(mainIntent)
                            finish()

                        }
                    }
                }
            } else {
                result.onFailure {
                    if (it is FirebaseAuthInvalidCredentialsException) {
                        val mainIntent =
                            VerificationActivity.newIntent(
                                this,
                                phoneNumber = null
                            )
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(mainIntent)
                        finish()
                    } else {
                        loadingDialog?.dismiss()
                        longToastShow("Invalid Cred!")


                    }

                }
            }


        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            googleSignInRequestCode -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)

                } catch (e: ApiException) {
                    e.printStackTrace()
                    e.message?.let { longToastShow(it) }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnSuccessListener {
                longToastShow("Login Successful")
                val mainIntent = Intent(this, VerificationActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(mainIntent)
                finish()
            }
            .addOnFailureListener {
                it.printStackTrace()
                it.message?.let { it1 -> longToastShow(it1) }
            }
    }
}