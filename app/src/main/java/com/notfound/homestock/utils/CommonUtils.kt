package com.notfound.homestock.utils

import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.models
import com.notfound.homestock.R
import com.notfound.homestock.base.AppContext

/**
 *
 * @Date 2024/02/02 10:11
 *
 * @Description
 */
object CommonUtils {
    fun getString(id: Int): String {
        return try {
            AppContext.application.resources.getString(id)
        } catch (e: Exception) {
            ""
        }
    }

    fun toast(id: Int) {
        Toast.makeText(AppContext.application, getString(id), Toast.LENGTH_SHORT).show()
    }

    fun createNavOptions(): NavOptions = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right

        }
    }

    fun showGuide(rv: RecyclerView, callback: (t:Int) -> Unit) {
        var guild = false
        val viewTreeObserver = ViewTreeObserver.OnDrawListener {
            rv.models?.let {
                if (it.isNotEmpty() && !guild) {
                    guild = true
                    rv.layoutManager?.let { layoutManager ->
                        if (layoutManager is LinearLayoutManager) {
                            layoutManager.findFirstVisibleItemPosition()
                                .takeIf { it != -1 }?.let { p ->
                                    callback.invoke(p)
                                }
                        }
                    }
                }
            }
        }
        rv.viewTreeObserver.addOnDrawListener(viewTreeObserver)
    }

}