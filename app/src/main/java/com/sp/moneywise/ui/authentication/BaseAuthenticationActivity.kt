package com.sp.moneywise.ui.authentication

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.sp.moneywise.ui.BaseActivity

open class BaseAuthenticationActivity : BaseActivity() {

    lateinit var baseAuthenticationViewModel: BaseAuthenticationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseAuthenticationViewModel =
            ViewModelProvider(this)[BaseAuthenticationViewModel::class.java]
        Log.e("Hello", baseAuthenticationViewModel.toString())
    }
}