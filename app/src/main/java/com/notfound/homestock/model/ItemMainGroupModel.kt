package com.notfound.homestock.model

import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import com.drake.brv.item.ItemPosition
import com.notfound.homestock.R
import com.notfound.homestock.bean.GroupBean
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.databinding.ItemMainGroupBinding
import com.notfound.homestock.utils.DataUtils

/**
 * @param numSoon 即将过期的子项数量
 * @param numAlready 已过期的子项数量
 */
open class ItemMainGroupModel(
    val bean: GroupBean,
    val subList: List<Any>,
    private val numSoon: Int,
    private val numAlready: Int,
) : ItemExpand, ItemPosition, ItemBind, Comparable<ItemMainGroupModel>,
    BaseObservable() {

    companion object {
        fun buildModel(): MutableList<ItemMainGroupModel> {
            val groupList = mutableListOf<ItemMainGroupModel>()
            val objectList = DataUtils.getItemData()

            DataUtils.getGroupData().forEach { groupBean ->
                val subList = mutableListOf<ItemInfoBean>()
                var tempNumSoon = 0
                var tempNumAlready = 0
                objectList.forEach { itemBean ->
                    if (itemBean.groupId == groupBean.id) {
                        when (ItemObjectModel.getExpirationStatus(itemBean)) {
                            ExpirationStatus.DEFAULT -> {

                            }

                            ExpirationStatus.EXPIRES_SOON -> {
                                tempNumSoon++
                            }

                            ExpirationStatus.EXPIRED -> {
                                tempNumAlready++
                            }
                        }
                        subList.add(itemBean)
                    }
                }
                groupList.add(
                    ItemMainGroupModel(
                        bean = groupBean,
                        subList = ItemGroupObjectModel.buildModel(
                            DataUtils.sortItemInfoBean(
                                subList
                            )
                        ),
                        numSoon = tempNumSoon,
                        numAlready = tempNumAlready,
                    )
                )
            }
            val result = mutableListOf<ItemMainGroupModel>()
            groupList.sorted()
            groupList.forEach {
                if (it.subList.isNotEmpty()) {
                    result.add(it)
                }
            }
            return result
        }
    }

    override var itemGroupPosition: Int = 0
    override var itemExpand: Boolean = false
        set(value) {
            field = value
            notifyChange()
        }

    override fun getItemSublist(): List<Any?>? {
        return subList
    }

    override var itemPosition: Int = 0

    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        DataBindingUtil.bind<ItemMainGroupBinding>(vh.itemView)?.let {
            it.ivArrow.setBackgroundResource(if (itemExpand) R.drawable.ic_arrow_nested_expand else R.drawable.ic_arrow_nested_collapse)
            it.clGroupRoot.setBackgroundResource(if (itemExpand) R.drawable.shape_dialog_add_bg_white_top_radius else R.drawable.shape_dialog_add_bg_white)
            it.vDividing.isInvisible = !itemExpand
            it.tvTitle.text = this.bean.name
            it.tvNumSoon.apply {
                text = this@ItemMainGroupModel.numSoon.toString()
                isVisible = this@ItemMainGroupModel.numSoon > 0
            }
            it.tvNumExpired.apply {
                text = this@ItemMainGroupModel.numAlready.toString()
                isVisible = this@ItemMainGroupModel.numAlready > 0
            }
            it.tvNum.apply {
                isVisible = this@ItemMainGroupModel.subList.isNotEmpty()
                text = String.format("[%d]", this@ItemMainGroupModel.subList.size)
            }
        }
    }

    override fun compareTo(other: ItemMainGroupModel): Int {
        val indexC = this.bean.index.compareTo(other.bean.id)
        return if (indexC == 0) this.bean.time.compareTo(other.bean.time) else indexC
    }
}