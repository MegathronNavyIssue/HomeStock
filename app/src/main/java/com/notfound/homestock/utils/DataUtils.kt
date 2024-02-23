package com.notfound.homestock.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.notfound.homestock.R
import com.notfound.homestock.bean.GroupBean
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.bean.ShoppingCartBean
import com.notfound.homestock.model.ShowModel
import com.notfound.homestock.viewmodel.RvViewModel

object DataUtils {
    // 首页数据
    private const val ITEM_INFO_DATA = "item_info_list"

    // 备货清单数据
    private const val SHOPPING_CART_DATA = "shopping_cart_data"

    // 分组数据
    private const val GROUP_DATA = "group_data"

    // 引导展示标志 - 首页
    const val GUIDE_HOME = "guide_home"

    // 引导展示标志 - 备货清单页面
    const val GUIDE_SHOPPING_CART = "guide_shopping_cart"

    // 引导展示标志 - 管理分组页面
    const val GUIDE_MANAGE_GROUP = "guide_manage_group"

    // 过期前多少天进入临期提醒阶段
    private const val NOTICE_TIME = "notice_time"

    // 显示模式
    private const val SHOW_MODEL = "show_model"

    init {
        setDefaultData()
    }

    private fun setDefaultData() {
        // 设置默认分组
        if (getGroupData().isEmpty()) {
            setGroupData(mutableListOf(GroupBean.getEmptyGroupBean()))
        }
    }

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

    fun getItemData(): MutableList<ItemInfoBean> {
        SharedPreferencesUtils.getString(ITEM_INFO_DATA)?.let {
            return Gson().fromJson<MutableList<ItemInfoBean>?>(
                it,
                object : TypeToken<List<ItemInfoBean>>() {}.type
            ).toMutableList()
        }
        return mutableListOf()
    }

    // 保存数据
    fun setItemData(list: List<ItemInfoBean>) {
        SharedPreferencesUtils.save(ITEM_INFO_DATA, Gson().toJson(list))
    }

    // 增加数据并且刷新UI
    fun addItemRefresh(item: ItemInfoBean, activity: AppCompatActivity) {
        saveItemInfoRefresh(
            getItemData().apply { this.add(item) },
            activity
        )
    }

    // 删除数据并且刷新UI
    fun removeItemRefresh(id: Long, activity: AppCompatActivity) {
        val list = getItemData()

        list.find { it.id == id }?.let {
            list.remove(it)
            saveItemInfoRefresh(list, activity)
        }
    }

    // 更新数据并且刷新UI
    fun updateItemRefresh(item: ItemInfoBean, activity: AppCompatActivity) {
        val list = getItemData()
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
    fun clear(activity: AppCompatActivity) {
        saveItemInfoRefresh(mutableListOf(), activity)
        saveShoppingCartInfoRefresh(mutableListOf(), activity)
        setGroupData(mutableListOf())
        setDefaultData()
    }

    // 获取过期前临期阶段天数
    fun getNoticeTime(): Int {
        SharedPreferencesUtils.getInt(NOTICE_TIME)?.takeIf { it >= 0 }?.let {
            return it
        }
        return 30
    }

    // 设置过期前临期阶段天数
    fun setNoticeTime(day: Int) {
        if (day >= 1) {
            SharedPreferencesUtils.save(NOTICE_TIME, day)
        }
    }

    // 获取显示模式
    fun getShowModel(): Int {
        SharedPreferencesUtils.getInt(SHOW_MODEL)?.takeIf { it != -1 }?.let {
            return it
        }
        return ShowModel.DEFAULT.id
    }

    // 设置过期前临期阶段天数
    fun setShowModel(model: Int) {
        SharedPreferencesUtils.save(SHOW_MODEL, model)
    }

    // 获取分组
    fun getGroupData(): MutableList<GroupBean> {
        SharedPreferencesUtils.getString(GROUP_DATA)?.let {
            return Gson().fromJson<MutableList<GroupBean>?>(
                it,
                object : TypeToken<List<GroupBean>>() {}.type
            ).toMutableList()
        } ?: kotlin.run {
            return mutableListOf()
        }
    }

    // 保存分组
    fun setGroupData(list: MutableList<GroupBean>) {
        SharedPreferencesUtils.save(GROUP_DATA, Gson().toJson(list))
    }

}