package com.sp.moneywise.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sp.moneywise.datalayer.AppDatabase

class DashboardViewModelFactory(private val myAppDatabase: AppDatabase?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(myAppDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}