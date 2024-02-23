package com.notfound.homestock.model

import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import com.notfound.homestock.R
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.databinding.ItemObjectStyleIiiBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ItemGroupObjectModel(
    override var itemGroupPosition: Int = 0,
    override var itemExpand: Boolean = false,
    itemInfoBean: ItemInfoBean,
) : ItemExpand, ItemBind, ItemObjectModel(itemGroupPosition, itemExpand, itemInfoBean) {

    companion object {
        fun buildModel(data: List<ItemInfoBean>): MutableList<ItemGroupObjectModel> {
            val list = mutableListOf<ItemGroupObjectModel>()
            for (obj in data) {
                list.add(
                    ItemGroupObjectModel(
                        itemInfoBean = obj,
                    )
                )
            }
            return list
        }
    }

    override fun getItemSublist(): List<Any>? {
        return null
    }

    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        DataBindingUtil.bind<ItemObjectStyleIiiBinding>(vh.itemView)?.let {
            it.tvName.text = itemInfoBean.name
            it.tvNum.text = itemInfoBean.num.toString()
            val currentTime = System.currentTimeMillis()
            val formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
            it.tvEditTime.text = String.format(
                vh.context.resources.getString(R.string.text_item_info_bean_edit_time),
                formatter.format(Instant.ofEpochMilli(itemInfoBean.editTime))
            )
            it.tvExpirationTime.text = String.format(
                vh.context.resources.getString(R.string.text_item_info_bean_expiration_time),
                formatter.format(Instant.ofEpochMilli(itemInfoBean.expirationTime))
            )
            if (itemInfoBean.expirationTime != 0L) {
                it.tvExpirationTime.isVisible = true
                when (getExpirationStatus(this.itemInfoBean)) {
                    ExpirationStatus.DEFAULT -> {
                        it.vStatus.setBackgroundResource(R.color.status_not_yet)
                    }

                    ExpirationStatus.EXPIRES_SOON -> {
                        it.vStatus.setBackgroundResource(R.color.status_soon)
                    }

                    ExpirationStatus.EXPIRED -> {
                        it.vStatus.setBackgroundResource(R.color.status_already)
                    }
                }
            } else {
                it.tvExpirationTime.isVisible = false
                it.vStatus.setBackgroundResource(R.color.status_not_yet)
            }
            val data = vh.adapter.models
            var temp: List<Any?>? = null
            data?.forEach { obj ->
                if (obj is ItemMainGroupModel && obj.bean.id == this.itemInfoBean.groupId) {
                    temp = obj.getItemSublist()
                }
            }
            temp?.let { t ->
                it.vFoot.isVisible = t.indexOf(this) == t.size - 1
            }
        }

    }
}