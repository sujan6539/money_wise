package com.sp.moneywise.ui.transaction

import androidx.annotation.ColorRes
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sp.moneywise.BR

class BottomSheetLayoutBaseObservable : BaseObservable() {

    @Bindable
    @ColorRes
    var background: Int = android.R.color.holo_green_light
        set(value) {
            field = value
            notifyPropertyChanged(BR.background)
        }
}