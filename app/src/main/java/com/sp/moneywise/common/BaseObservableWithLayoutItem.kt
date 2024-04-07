package com.sp.moneywise.common

import androidx.annotation.LayoutRes

import androidx.databinding.BaseObservable


/**
 * @author acampbell
 */
abstract class BaseObservableLayoutItem : BaseObservable() {

    @get:LayoutRes
    abstract val layoutId: Int
}