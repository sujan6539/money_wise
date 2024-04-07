package com.sp.moneywise.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sp.moneywise.datalayer.AppDatabase
import com.sp.moneywise.datalayer.model.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel(private val myAppDatabase: AppDatabase?) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    private val _text = MutableLiveData<String>().apply {
        value = "All Transactions"
    }
    val text: LiveData<String> = _text

    private val _transactionLiveData = myAppDatabase?.transactionDao()?.getAllNotes()

    val transactionLiveData: LiveData<List<TransactionEntity>>? = _transactionLiveData

    fun addTransaction(transactionEntity: TransactionEntity){
        viewModelScope.launch {
            myAppDatabase?.transactionDao()?.insert(transactionEntity)
        }
    }
}