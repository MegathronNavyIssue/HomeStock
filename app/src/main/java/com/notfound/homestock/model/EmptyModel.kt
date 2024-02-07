package com.notfound.homestock.model

import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.notfound.homestock.databinding.ItemEmptyBinding

/**
 *
 * @Date 2024/02/01 16:40
 *
 * @Description
 */
class EmptyModel(private val h: Int) : ItemBind {
    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        DataBindingUtil.bind<ItemEmptyBinding>(vh.itemView)?.let {
            it.clRoot.updateLayoutParams {
                this.height = h
            }
        }
    }
}