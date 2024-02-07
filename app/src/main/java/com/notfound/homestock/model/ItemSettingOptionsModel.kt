package com.notfound.homestock.model

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.lxj.xpopup.XPopup
import com.notfound.homestock.BuildConfig
import com.notfound.homestock.R
import com.notfound.homestock.base.AppContext
import com.notfound.homestock.databinding.ItemSettingOptionsBinding
import com.notfound.homestock.utils.CommonUtils
import com.notfound.homestock.utils.DataUtils

class ItemSettingOptionsModel(
    val title: String,
    val content: String,
    val action: String,
    val showLine: Boolean = true,
    val activity: Activity,
) : ItemBind {
    companion object {
        private const val ACTION_CLEAR: String = "action_clear"
        private const val ACTION_EMPTY: String = "action_empty"
        private const val ACTION_GITHUB: String = "action_github"
        private const val ACTION_RESOURCE: String = "action_resource"
        private val hrefList = mutableListOf<HrefBean>()

        private class HrefBean(
            val title: String,
            val url: String,
        )

        init {
            // 免费资源来源要求标注出处
            hrefList.clear()
            hrefList.add(HrefBean("icons by [Bing AI]", "https://www.bing.com/"))
            hrefList.add(HrefBean("icons by [Google Fonts]", "https://fonts.google.com/"))
            hrefList.add(HrefBean("images by [Pixabay]", "https://pixabay.com/"))
            hrefList.add(HrefBean("stickers by [Flaticon]", "https://www.flaticon.com/"))
        }

        fun getSettings(activity: Activity): List<ItemSettingOptionsModel> {
            return mutableListOf<ItemSettingOptionsModel>().apply {
                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_version),
                        content = BuildConfig.VERSION_NAME,
                        action = ACTION_EMPTY,
                        activity = activity,
                    )
                )
                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_settings_resource),
                        content = "",
                        action = ACTION_RESOURCE,
                        activity = activity,
                    )
                )

                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_settings_github),
                        content = "",
                        action = ACTION_GITHUB,
                        activity = activity,
                    )
                )
                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_settings_clear),
                        content = "",
                        action = ACTION_CLEAR,
                        showLine = false,
                        activity = activity,
                    )
                )
            }
        }
    }

    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        DataBindingUtil.bind<ItemSettingOptionsBinding>(vh.itemView)?.let {
            it.tvTitle.text = title
            it.tvContent.text = content
            it.vDividing.isInvisible = !showLine
        }
    }

    fun onClick() {
        when (action) {
            ACTION_CLEAR -> {
                DataUtils.clear(activity as AppCompatActivity)
                CommonUtils.toast(R.string.text_settings_clear_success)
            }

            ACTION_GITHUB -> {
                AppContext.application.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/MegathronNavyIssue/HomeStock")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
            }

            ACTION_RESOURCE -> {
                if (hrefList.isNotEmpty()) {
                    XPopup.Builder(this.activity)
                        .maxHeight(800)
                        .asCenterList("", hrefList.map { it.title }.toTypedArray())
                        { position, _ ->
                            AppContext.application.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(hrefList[position].url)
                                ).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                        }
                        .show()
                }
            }

            ACTION_EMPTY -> {

            }
        }
    }

}