package com.sp.moneywise.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sp.moneywise.datalayer.AppDatabase

class TransactionViewModelFactory(private val myAppDatabase: AppDatabase?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(myAppDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}