package com.sp.moneywise.ui.profile

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.sp.moneywise.BR

class ProfileFragmentBaseObservable : BaseObservable() {

    @Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }


    interface ProfileCallbacks {
        fun onLogOutClicked()
        fun onDeleteAccountClicked()
        fun onSettingsClicked()
        fun onExportDataClicked()

        fun updateEmail()
        fun updatePassword()
    }

}