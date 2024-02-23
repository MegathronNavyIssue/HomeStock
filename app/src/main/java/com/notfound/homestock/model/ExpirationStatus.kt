package com.notfound.homestock.model

enum class ExpirationStatus(val statusCode:Int,val statusDes:String) {
    // 未过期
    DEFAULT(0,""),
    // 即将过期
    EXPIRES_SOON(1,""),
    // 已过期
    EXPIRED(2,""),
}