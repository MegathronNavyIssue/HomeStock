package com.notfound.homestock.bean

import com.notfound.homestock.R
import com.notfound.homestock.utils.CommonUtils

/**
 * @param index 用于用户自定义排序
 * @param time 添加分组的时间，用于index相等时排序
 *
 */
data class GroupBean(
    val id: Long,
    var name: String,
    var index: Int = GROUP_INDEX_DEFAULT,
    var time: Long = System.currentTimeMillis(),
) {
    companion object {
        const val GROUP_EMPTY_ID = 0L
        const val GROUP_INDEX_DEFAULT = 0
        fun getEmptyGroupBean() = GroupBean(
            id = GROUP_EMPTY_ID,
            name = CommonUtils.getString(R.string.text_group_empty)
        )
    }
}