package com.sp.moneywise.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sp.moneywise.datalayer.AppDatabase
import com.sp.moneywise.datalayer.model.TransactionEntity
import com.sp.moneywise.datalayer.model.TransactionType

class DashboardViewModel(private val myAppDatabase: AppDatabase?) : ViewModel() {


    private val _transactionLiveData = myAppDatabase?.transactionDao()?.getAllNotes()

    val transactionLiveData: LiveData<List<TransactionEntity>>? = _transactionLiveData


    fun getMonthSummary(category: TransactionType) =
        myAppDatabase?.transactionDao()?.getCurrMonthSummary(category)

    fun getAccountBalance() = myAppDatabase?.transactionDao()?.getAccountBalance()
}