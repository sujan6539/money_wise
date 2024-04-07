/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sp.moneywise.common

import androidx.databinding.ViewDataBinding
import com.sp.moneywise.BR
import java.util.Collections

/**
 * An abstraction over [BaseDataBoundAdapter] that keeps the list of children and can
 * support multiple item types.
 *
 *
 * This class relies on all layouts using the "data" variable to represent the item.
 *
 *
 * Although this class by itself just exists for demonstration purposes, it might be a useful idea
 * for an application to have a generic naming convention for their items to scale lists with
 * many view types.
 *
 *
 * Note that, by using this, you lose the compile time type checking for mapping your items to
 * layout files but at run time, it will still be checked. See
 * [androidx.databinding.ViewDataBinding.setVariable] for details.
 */
class MultiTypeDataBoundAdapter(vararg items: Any?) : BaseDataBoundAdapter<ViewDataBinding?>() {
    private val mItems: MutableList<Any?> = ArrayList()

    init {
        Collections.addAll(mItems, *items)
    }

    override fun bindItem(
        holder: DataBoundViewHolder<ViewDataBinding?>?,
        position: Int,
        payloads: List<Any>?,
    ) {
        holder?.binding?.setVariable(BR.data, mItems[position])
    }

    override fun getItemLayoutId(position: Int): Int {
        val item = getItem(position)
        return if (item is BaseObservableLayoutItem) {
            item.layoutId
        } else {
            throw IllegalArgumentException("Item must be BaseObservableLayoutItem")
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun getItem(position: Int): Any? {
        return mItems[position]
    }

    fun addItem(item: Any) {
        mItems.add(item)
        notifyItemInserted(mItems.size - 1)
    }

    fun addItem(position: Int, item: Any) {
        mItems.add(position, item)
        notifyItemInserted(position)
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }
}
