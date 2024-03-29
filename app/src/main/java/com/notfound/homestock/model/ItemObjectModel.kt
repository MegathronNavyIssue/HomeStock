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
    val itemInfoBean: ItemInfoBean,
) : ItemExpand, ItemBind {

    companion object {
        fun buildModel(data: List<ItemInfoBean>): MutableList<ItemObjectModel> {
            val list = mutableListOf<ItemObjectModel>()
            for (obj in data) {
                list.add(
                    ItemObjectModel(
                        itemInfoBean = obj,
                    )
                )
            }
            return list
        }

        fun getExpirationStatus(obj: ItemInfoBean): ExpirationStatus {
            val diff = TimeUnit.MILLISECONDS.toDays(obj.expirationTime - System.currentTimeMillis())
            return if (obj.expirationTime == 0L) {
                ExpirationStatus.DEFAULT
            } else if (diff < 0) {
                ExpirationStatus.EXPIRED
            } else if (diff < DataUtils.getNoticeTime()) {
                ExpirationStatus.EXPIRES_SOON
            } else {
                ExpirationStatus.DEFAULT
            }
        }
    }

    override fun getItemSublist(): List<Any>? {
        return null
    }

    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        DataBindingUtil.bind<ItemObjectStyleIiBinding>(vh.itemView)?.let {
            it.tvName.text = this.itemInfoBean.name
            it.tvNum.text = this.itemInfoBean.num.toString()
            val formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault())
            it.tvEditTime.text = String.format(
                vh.context.resources.getString(R.string.text_item_info_bean_edit_time),
                formatter.format(Instant.ofEpochMilli(this.itemInfoBean.editTime))
            )
            it.tvExpirationTime.text = String.format(
                vh.context.resources.getString(R.string.text_item_info_bean_expiration_time),
                formatter.format(Instant.ofEpochMilli(this.itemInfoBean.expirationTime))
            )
            if (this.itemInfoBean.expirationTime != 0L) {
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
        }

    }
}