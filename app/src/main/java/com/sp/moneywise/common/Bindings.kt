package com.sp.moneywise.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

object Bindings {

    @JvmStatic
    @BindingAdapter("srcCompat")
    fun loadImage(view: ImageView, @DrawableRes srcCompat: Int) {
        view.setImageDrawable(ContextCompat.getDrawable(view.context, srcCompat))
    }


    @JvmStatic
    @BindingAdapter("background")
    fun loadColor(view: View, @ColorRes color: Int) {
        view.setBackgroundColor(ContextCompat.getColor(view.context, color))
    }

    @JvmStatic
    @BindingAdapter("textColor")
    fun loadTextColor(view: TextView, @ColorRes color: Int) {
        view.setTextColor(ContextCompat.getColor(view.context, color))
    }
}