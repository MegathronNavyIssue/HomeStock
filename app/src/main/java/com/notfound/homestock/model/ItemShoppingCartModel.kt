package com.notfound.homestock.model

import androidx.databinding.DataBindingUtil
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemSwipe
import com.notfound.homestock.bean.ShoppingCartBean
import com.notfound.homestock.databinding.ItemShoppingCartObjectBinding

class ItemShoppingCartModel(
    override var itemOrientationSwipe: Int = ItemOrientation.HORIZONTAL,
    var isChecked: Boolean = false,
    val bean: ShoppingCartBean,
) : ItemBind, ItemSwipe {

    companion object {
        fun buildModel(bean: ShoppingCartBean): ItemShoppingCartModel {
            return ItemShoppingCartModel(
                bean = bean
            )
        }
    }

    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        DataBindingUtil.bind<ItemShoppingCartObjectBinding>(vh.itemView)?.let {
            it.tvName.text = bean.name
            it.checkbox.isChecked = this.isChecked
        }
    }


}