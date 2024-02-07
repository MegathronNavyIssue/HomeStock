package com.notfound.homestock.base

import android.app.Application

/**
 *
 * @Date 2024/02/01 15:05
 *
 * @Description
 */
object AppContext {
    lateinit var application: Application
        private set

    fun init(application: Application) {
        this.application = application
    }
}