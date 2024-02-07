package com.notfound.homestock.model

import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import com.drake.brv.item.ItemHover
import com.drake.brv.item.ItemPosition
import com.notfound.homestock.R
import com.notfound.homestock.databinding.ItemMainGroupBinding

/**
 * unUsed
 */
open class ItemMainGroupModel : ItemExpand, ItemHover, ItemPosition, ItemBind, BaseObservable() {

    override var itemGroupPosition: Int = 0
    override var itemExpand: Boolean = false
        set(value) {
            field = value
            notifyChange()
        }

    override fun getItemSublist(): List<Any?>? {
        return sublist
    }

    var sublist: List<Any?> = mutableListOf()

    override var itemHover: Boolean = true
    override var itemPosition: Int = 0

    val title get() = "分组 [ $itemGroupPosition ]"

    val expandIcon get() = if (itemExpand) R.drawable.ic_arrow_nested_expand else R.drawable.ic_arrow_nested_collapse

    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        DataBindingUtil.bind<ItemMainGroupBinding>(vh.itemView)?.let {
            it.ivArrow.setBackgroundResource(expandIcon)
            it.tvTitle.text = title
        }
    }
}