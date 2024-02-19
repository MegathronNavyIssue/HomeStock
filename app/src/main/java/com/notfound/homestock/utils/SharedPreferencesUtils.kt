package com.notfound.homestock.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.notfound.homestock.base.AppContext
import timber.log.Timber

object SharedPreferencesUtils {
    private const val USER_CONFIG = "_data"
    private lateinit var instances: SharedPreferences

    fun init() {
        instances = AppContext.application.getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE)
    }

    private fun initCheck(): Boolean {
        val status = ::instances.isInitialized
        if (!status) {
            Timber.e("SharedPreferences not yet initialized!")
        }
        return status
    }

    fun save(key: String, value: Any?) {
        if (!initCheck()) {
            return
        }
        if (TextUtils.isEmpty(key) || value == null) {
            return
        }
        instances.edit()?.apply {
            when (value) {
                is String -> this.putString(key, value)
                is Boolean -> this.putBoolean(key, value)
                is Float -> this.putFloat(key, value)
                is Int -> this.putInt(key, value)
                is Long -> this.putLong(key, value)
            }
        }?.apply()
    }


    fun getString(key: String): String? {
        if (!initCheck()) {
            return null
        }
        return instances.getString(key, null)
    }

    fun getInt(key: String): Int? {
        if (!initCheck()) {
            return null
        }
        return instances.getInt(key, -1)
    }


    fun getBoolean(key: String): Boolean? {
        if (!initCheck()) {
            return null
        }
        return instances.getBoolean(key, false)
    }


    fun clear() {
        instances.edit().clear().apply()
    }

}