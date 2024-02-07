package com.notfound.homestock.bean

data class ItemInfoBean(
    // id 用于更新数据
    val id :Long = 0,
    // 物品名
    val name: String = "",
    // 数量
    val num: Int = 0,
    // 编辑时间
    val editTime: Long = 0,
    // 过期时间
    val expirationTime: Long = 0,
    // 分组(未使用)
    val group: String = "",
)