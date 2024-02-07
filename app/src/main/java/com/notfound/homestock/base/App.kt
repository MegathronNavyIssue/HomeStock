package com.notfound.homestock.base

import android.app.Application
import com.notfound.homestock.BuildConfig
import com.notfound.homestock.utils.SharedPreferencesUtils
import timber.log.Timber


/**
 *
 * @Date 2024/01/30 14:07
 *
 * @Description
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
        SharedPreferencesUtils.init()
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "${element.className}.${element.methodName}"
                }
            })
        }
    }
}