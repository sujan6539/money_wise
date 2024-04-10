package com.sp.moneywise.ui.authentication

import android.os.Bundle
import androidx.activity.viewModels
import com.sp.moneywise.ui.BaseActivity

open class BaseAuthenticationActivity : BaseActivity() {

    val baseAuthenticationViewModel: BaseAuthenticationViewModel by viewModels<BaseAuthenticationViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}