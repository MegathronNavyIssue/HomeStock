package com.notfound.homestock.model

import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import com.notfound.homestock.R
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.databinding.ItemObjectStyleIiBinding
import com.notfound.homestock.utils.DataUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

open class ItemObjectModel(
    override var itemGroupPosition: Int = 0,
    override var itemExpand: Boolean = false,
    val id: Long,
    val name: String,
    val num: Int,
    val editTime: Long,
    val expirationTime: Long,
    val itemInfoBean: ItemInfoBean,
) : ItemExpand, ItemBind {

    companion object {
        fun buildModel(data: List<ItemInfoBean>): MutableList<ItemObjectModel> {
            val list = mutableListOf<ItemObjectModel>()
            for (obj in data) {
                list.add(
                    ItemObjectModel(
                        id = obj.id,
                        name = obj.name,
                        num = obj.num,
                        editTime = obj.editTime,
                        expirationTime = obj.expirationTime,
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
        DataBindingUtil.bind<ItemObjectStyleIiBinding>(vh.itemView)?.let {
            it.tvName.text = name
            it.tvNum.text = num.toString()
            val currentTime = System.currentTimeMillis()
            val formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
            it.tvEditTime.text = String.format(
                vh.context.resources.getString(R.string.text_item_info_bean_edit_time),
                formatter.format(Instant.ofEpochMilli(editTime))
            )
            it.tvExpirationTime.text = String.format(
                vh.context.resources.getString(R.string.text_item_info_bean_expiration_time),
                formatter.format(Instant.ofEpochMilli(expirationTime))
            )
            if (expirationTime != 0L) {
                it.tvExpirationTime.isVisible = true
                val diff = TimeUnit.MILLISECONDS.toDays(expirationTime - currentTime)
                if (diff < 0) {
                    it.vStatus.setBackgroundResource(R.color.status_already)
                } else if (diff < DataUtils.getNoticeTime()) {
                    it.vStatus.setBackgroundResource(R.color.status_soon)
                } else {
                    it.vStatus.setBackgroundResource(R.color.status_not_yet)
                }
            } else {
                it.tvExpirationTime.isVisible = false
                it.vStatus.setBackgroundResource(R.color.status_not_yet)
            }
        }

    }
}