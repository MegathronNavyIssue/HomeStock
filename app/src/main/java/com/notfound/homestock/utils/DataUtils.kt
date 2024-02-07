package com.notfound.homestock.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.notfound.homestock.R
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.bean.ShoppingCartBean
import com.notfound.homestock.viewmodel.RvViewModel

object DataUtils {
    private const val ITEM_INFO_DATA = "item_info_list"
    private const val SHOPPING_CART_DATA = "shopping_cart_data"
    const val GUIDE_HOME = "guide_home"
    const val GUIDE_SHOPPING_CART = "guide_shopping_cart"

    // 保存数据并且通知liveData刷新
    private fun saveItemInfoRefresh(
        list: List<ItemInfoBean>,
        activity: AppCompatActivity
    ) {
        SharedPreferencesUtils.save(ITEM_INFO_DATA, Gson().toJson(list))
        ViewModelProvider(activity)[RvViewModel::class.java].itemInfoData.postValue(list)
    }

    // 保存数据并且通知liveData刷新
    private fun saveShoppingCartInfoRefresh(
        list: List<ShoppingCartBean>,
        activity: AppCompatActivity
    ) {
        SharedPreferencesUtils.save(SHOPPING_CART_DATA, Gson().toJson(list))
        ViewModelProvider(activity)[RvViewModel::class.java].shoppingCartData.postValue(list)
    }

    fun loadItemInfo(): MutableList<ItemInfoBean> {
        SharedPreferencesUtils.getString(ITEM_INFO_DATA)?.let {
            return Gson().fromJson<MutableList<ItemInfoBean>?>(
                it,
                object : TypeToken<List<ItemInfoBean>>() {}.type
            ).toMutableList()
        }
        return mutableListOf()
    }

    // 增加数据并且刷新UI
    fun addItemRefresh(item: ItemInfoBean, activity: AppCompatActivity) {
        saveItemInfoRefresh(
            loadItemInfo().apply { this.add(item) },
            activity
        )
    }

    // 删除数据并且刷新UI
    fun removeItemRefresh(p: Int, activity: AppCompatActivity) {
        loadItemInfo().takeIf { it.size > p }?.apply {
            removeAt(p)
        }?.let {
            saveItemInfoRefresh(
                it,
                activity
            )
        }
    }

    // 更新数据并且刷新UI
    fun updateItemRefresh(item: ItemInfoBean, activity: AppCompatActivity) {
        val list = loadItemInfo()
        val p = list.indexOfFirst { it.id == item.id }
        if (p != -1) {
            list[p] = item
            saveItemInfoRefresh(list, activity)
            CommonUtils.toast(R.string.text_item_edit_success)
        } else {
            CommonUtils.toast(R.string.text_error)
        }
    }

    // 排序
    fun sortItemInfoBean(data: List<ItemInfoBean>): MutableList<ItemInfoBean> {
        val a = mutableListOf<ItemInfoBean>()
        val b = mutableListOf<ItemInfoBean>()
        val result = mutableListOf<ItemInfoBean>()
        data.forEach {
            if (it.expirationTime != 0L) {
                a.add(it)
            } else {
                b.add(it)
            }
        }
        result.addAll(a.apply {
            sortBy { it.expirationTime }
        })
        result.addAll(b.apply {
            sortByDescending { it.editTime }
        })
        return result
    }

    // 读取备货清单列表
    fun loadShoppingCartObject(): MutableList<ShoppingCartBean> {
        SharedPreferencesUtils.getString(SHOPPING_CART_DATA)?.let {
            return Gson().fromJson<MutableList<ShoppingCartBean>?>(
                it,
                object : TypeToken<List<ShoppingCartBean>>() {}.type
            ).toMutableList()
        }
        return mutableListOf()
    }

    // 添加备货清单
    fun addShoppingCartObject(item: ShoppingCartBean, activity: AppCompatActivity) {
        saveShoppingCartInfoRefresh(
            loadShoppingCartObject().apply { this.add(item) },
            activity
        )
    }

    // 更新备货清单
    fun updateShoppingCartObjectList(list: List<ShoppingCartBean>, activity: AppCompatActivity) {
        saveShoppingCartInfoRefresh(list, activity)
    }

    // 是否展示过引导页
    fun loadGuide(key: String): Boolean {
        SharedPreferencesUtils.getBoolean(key)?.let {
            return it
        }
        return false
    }

    // 已展示过引导页
    fun setGuide(key: String) {
        SharedPreferencesUtils.save(key, true)
    }

    // 删除数据
    fun clear(activity: AppCompatActivity){
        saveItemInfoRefresh(mutableListOf(),activity)
        saveShoppingCartInfoRefresh(mutableListOf(),activity)
    }

}