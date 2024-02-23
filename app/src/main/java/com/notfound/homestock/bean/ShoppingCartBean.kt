package com.notfound.homestock.bean

import com.notfound.homestock.R
import com.notfound.homestock.utils.CommonUtils

data class ShoppingCartBean(
    // id 用于更新数据
    val id: Long = 0,
    // 物品名
    val name: String = "",
) {
    companion object {
        const val DEFAULT_ID = 0L
        fun getDefaultShoppingCartBean() = ShoppingCartBean(
            id = DEFAULT_ID,
            name = CommonUtils.getString(R.string.text_shopping_cart_default),
        )
    }
}