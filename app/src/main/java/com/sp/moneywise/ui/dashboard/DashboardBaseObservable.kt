package com.sp.moneywise.ui.dashboard

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sp.moneywise.BR
import com.sp.moneywise.common.MultiTypeDataBoundAdapter

class DashboardBaseObservable : BaseObservable() {

    @Bindable
    var adapter = MultiTypeDataBoundAdapter()

    @Bindable
    var accountBalance: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.accountBalance)
        }


    fun addItems(list: List<ItemTransactionBaseObservable>) {
        list.forEach {
            adapter.addItem(it)
        }
    }

    fun addItems(baseObservableLayoutItem: ItemTransactionBaseObservable) {
        adapter.addItem(baseObservableLayoutItem)
    }
}