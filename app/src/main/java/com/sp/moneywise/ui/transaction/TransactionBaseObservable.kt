package com.sp.moneywise.ui.transaction

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sp.moneywise.common.MultiTypeDataBoundAdapter
import com.sp.moneywise.ui.dashboard.ItemTransactionBaseObservable

class TransactionBaseObservable : BaseObservable() {

    @Bindable
    var adapter = MultiTypeDataBoundAdapter()


    fun addItems(list: List<ItemTransactionBaseObservable>) {
        list.forEach {
            adapter.addItem(it)
        }
    }

    fun addItems(baseObservableLayoutItem: ItemTransactionBaseObservable) {
        adapter.addItem(baseObservableLayoutItem)
    }
}