package com.sp.moneywise.ui.dashboard

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.databinding.Bindable
import com.sp.moneywise.R
import com.sp.moneywise.common.BaseObservableLayoutItem
import com.sp.moneywise.datalayer.model.Category
import com.sp.moneywise.datalayer.model.TransactionType

class ItemTransactionBaseObservable(
    @Bindable val title: String,
    @Bindable val amount: String,
    @Bindable val description: String,
    @Bindable val time: String,
    private val transactionType: TransactionType,
    category: Category
) : BaseObservableLayoutItem() {

    @Bindable
    @DrawableRes
    var image : Int = when(category){
        Category.TRANSPORT -> R.drawable.car
        Category.SHOPPING -> R.drawable.shopping_bag
        Category.RESTAURANT -> R.drawable.restaurant
        Category.SALARY -> R.drawable.salary
        Category.MISC -> R.drawable.home
    }

    @Bindable
    @ColorRes
    var color: Int = if (transactionType == TransactionType.INCOME) {
        android.R.color.holo_green_light
    } else {
        android.R.color.holo_red_light
    }


    override val layoutId: Int
        get() = R.layout.item_transaction
}