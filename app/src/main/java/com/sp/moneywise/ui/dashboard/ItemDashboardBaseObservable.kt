package com.sp.moneywise.ui.dashboard

import androidx.annotation.ColorRes
import androidx.databinding.Bindable
import com.sp.moneywise.BR
import com.sp.moneywise.R
import com.sp.moneywise.common.BaseObservableLayoutItem

class ItemDashboardBaseObservable(
    @Bindable val title: String,
    @Bindable @ColorRes val color: Int,
    isIncome: Boolean
) : BaseObservableLayoutItem() {


    @Bindable
    var totalValue: String = "100"
        set(value) {
            field = value
            notifyPropertyChanged(BR.totalValue)
        }


    @Bindable
    var image: Int = R.drawable.income
        set(value) {
            field = value
            notifyPropertyChanged(BR.image)
        }

    init {
        image = if (isIncome) {
            R.drawable.income
        } else {
            R.drawable.expense
        }

    }


    override val layoutId: Int
        get() = R.layout.item_dashboard_sum
}