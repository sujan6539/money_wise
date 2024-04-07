package com.sp.moneywise.ui.dashboard

import android.app.Dialog
import android.os.Bundle
import android.widget.LinearLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sp.moneywise.R
import com.sp.moneywise.databinding.ActivityDashboardBinding
import com.sp.moneywise.ui.BaseActivity

class DashboardActivity : BaseActivity(), OnLoadingUIUpdate {

    private lateinit var binding: ActivityDashboardBinding

    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_transaction,
                R.id.navigation_notifications,
                R.id.navigation_profile,
            )
        )

        loadingDialog = Dialog(this, R.style.DialogCustomTheme).apply {
            setContentView(R.layout.loading_dialog)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun showLoading() {
        loadingDialog?.show()
    }

    override fun dismiss() {
        loadingDialog?.hide()
    }
}

interface OnLoadingUIUpdate {
    fun showLoading()

    fun dismiss()
}