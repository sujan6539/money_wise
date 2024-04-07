package com.sp.moneywise.ui.login

import com.sp.moneywise.ui.BaseActivity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sp.moneywise.R
import com.sp.moneywise.isConnected
import com.sp.moneywise.longToastShow
import com.sp.moneywise.ui.verification.VerificationActivity
import com.sp.moneywise.validateConPassword
import com.sp.moneywise.validateEmail
import com.sp.moneywise.validateName
import com.sp.moneywise.validatePassword

class RegisterActivity : BaseActivity() {

    private val registerViewModel: RegisterViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val loadingDialog = Dialog(this, R.style.DialogCustomTheme).apply {
            setContentView(R.layout.loading_dialog)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
        }


        val signInABtn = findViewById<Button>(R.id.signInABtn)
        signInABtn.setOnClickListener {
            finish()
        }

        val edName = findViewById<TextInputEditText>(R.id.edName)
        val edNameL = findViewById<TextInputLayout>(R.id.edNameL)

        edName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateName(edName, edNameL)
            }

        })


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


        val edConPassword = findViewById<TextInputEditText>(R.id.edConPassword)
        val edConPasswordL = findViewById<TextInputLayout>(R.id.edConPasswordL)
        edConPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateConPassword(edPassword, edConPassword, edConPasswordL)
            }

        })


        val signUpBtn = findViewById<Button>(R.id.signUpBtn)
        signUpBtn.setOnClickListener {
            if (validateName(edName, edNameL)
                && validateEmail(edEmail, edEmailL)
                && validatePassword(edPassword, edPasswordL)
                && validateConPassword(edPassword, edConPassword, edConPasswordL)
            ) {
                if (isConnected(this)) {
                    loadingDialog.show()
                    var email  = edEmail.text.toString()
                    var password =  edPassword.text.toString()

                    registerViewModel.registerUser(
                        displayName = edName.text.toString(),
                        email = edEmail.text.toString(),
                        password = edPassword.text.toString()
                    ) { isSuccess, message ->
                        if (isSuccess) {
                            longToastShow("Register Successful")
                            loadingDialog.dismiss()
                            val mainIntent = VerificationActivity.newIntent(this, email, password, true)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(mainIntent)
                            finish()
                        } else {
                            loadingDialog.dismiss()
                            message?.let { it1 -> longToastShow(it1) }

                        }


                    }
                } else {
                    longToastShow("No Internet Connection!")
                }
            }

        }
    }


}