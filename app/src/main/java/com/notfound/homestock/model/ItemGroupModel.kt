package com.notfound.homestock.model

import androidx.databinding.DataBindingUtil
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.ItemOrientation
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemDrag
import com.notfound.homestock.bean.GroupBean
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.databinding.ItemGroupBinding
import com.notfound.homestock.utils.DataUtils

class ItemGroupModel(
    val bean: GroupBean,
    val itemCount: Int,
    override var itemOrientationDrag: Int = ItemOrientation.ALL
) : ItemDrag ,ItemBind {

    companion object {
        fun buildModel(): MutableList<ItemGroupModel> {
            val groupList = mutableListOf<ItemGroupModel>()
            val objectList = DataUtils.getItemData()

            DataUtils.getGroupData().let {
                it.forEach { groupBean ->
                    val subList = mutableListOf<ItemInfoBean>()
                    objectList.forEach { itemBean ->
                        if (itemBean.groupId == groupBean.id) {
                            subList.add(itemBean)
                        }
                    }
                    groupList.add(
                        ItemGroupModel(
                            bean = groupBean,
                            itemCount = subList.size,
                        )
                    )
                }
            }
            groupList.sortBy { it.bean.index }
            return groupList
        }
    }

    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        DataBindingUtil.bind<ItemGroupBinding>(vh.itemView)?.let {
            it.tvTitle.text = this.bean.name
            it.tvContent.text = String.format("[%d]",this.itemCount)
        }

    }
}