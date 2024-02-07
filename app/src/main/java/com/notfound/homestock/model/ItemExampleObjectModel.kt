package com.notfound.homestock.model

import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import com.notfound.homestock.R
import com.notfound.homestock.base.AppContext
import com.notfound.homestock.databinding.ItemObjectStyleIiBinding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

open class ItemExampleObjectModel(
    override var itemGroupPosition: Int = 0,
    override var itemExpand: Boolean = false,
    private val expirationTime: Long = 0,
) : ItemExpand, ItemBind {
    private val editTime = System.currentTimeMillis()
    override fun getItemSublist(): List<Any>? {
        return null
    }

    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        DataBindingUtil.bind<ItemObjectStyleIiBinding>(vh.itemView)?.let {
            it.tvNum.text = 10.toString()
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
            val diff = TimeUnit.MILLISECONDS.toDays(expirationTime - currentTime)
            if (diff < 0) {
                it.tvName.text = "这是过期样例"
                it.vStatus.setBackgroundResource(R.color.status_already)
            } else if (diff < 30) {
                it.tvName.text = "这是快过期样例"
                it.vStatus.setBackgroundResource(R.color.status_soon)
            } else {
                it.tvName.text = "这是常规样例"
                it.vStatus.setBackgroundResource(R.color.status_not_yet)
            }
        }

    }
}