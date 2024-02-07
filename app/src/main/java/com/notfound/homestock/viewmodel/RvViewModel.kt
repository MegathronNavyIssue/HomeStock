package com.notfound.homestock.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.bean.ShoppingCartBean

class RvViewModel : ViewModel() {
    private val _itemInfoData = MutableLiveData<List<ItemInfoBean>>()
    private val _shoppingCartData = MutableLiveData<List<ShoppingCartBean>>()

    val itemInfoData get() = _itemInfoData
    val shoppingCartData get() = _shoppingCartData
}