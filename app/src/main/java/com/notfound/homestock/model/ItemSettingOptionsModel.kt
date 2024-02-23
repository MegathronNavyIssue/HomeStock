package com.notfound.homestock.model

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.InputConfirmPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
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
    val activity: Activity,
    val group: Int,
) : ItemBind {
    companion object {
        // 清空缓存
        private const val ACTION_CLEAR: String = "action_clear"

        // 空动作
        private const val ACTION_EMPTY: String = "action_empty"

        // 跳转项目地址
        private const val ACTION_GITHUB: String = "action_github"

        // 展示资源来源
        private const val ACTION_RESOURCE: String = "action_resource"

        // 修改临期时间
        private const val ACTION_CHANGE_NOTICE_TIME: String = "action_change_notice_time"

        // 修改首页列表样式
        private const val ACTION_CHANGE_SHOW_MODEL: String = "action_change_show_model"

        // 管理分组
        private const val ACTION_MANAGE_GROUPS: String = "action_manage_groups"

        // 分组间隔
        private const val GROUP_DIVIDING: Int = 50

        fun getSettings(activity: Activity): List<ItemSettingOptionsModel> {
            return mutableListOf<ItemSettingOptionsModel>().apply {
                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_settings_show_model),
                        content = if (DataUtils.getShowModel() == ShowModel.GROUP.id) CommonUtils.getString(
                            R.string.text_settings_show_model_group
                        ) else CommonUtils.getString(R.string.text_settings_show_model_default),
                        action = ACTION_CHANGE_SHOW_MODEL,
                        activity = activity,
                        group = 0,
                    )
                )
                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_settings_manage_groups),
                        content = "",
                        action = ACTION_MANAGE_GROUPS,
                        activity = activity,
                        group = 0,
                    )
                )
                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_settings_notice_time),
                        content = String.format(
                            CommonUtils.getString(R.string.text_settings_notice_time_unit),
                            DataUtils.getNoticeTime()
                        ),
                        action = ACTION_CHANGE_NOTICE_TIME,
                        activity = activity,
                        group = 0,
                    )
                )
                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_version),
                        content = BuildConfig.VERSION_NAME,
                        action = ACTION_EMPTY,
                        activity = activity,
                        group = 1,
                    )
                )
                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_settings_resource),
                        content = "",
                        action = ACTION_RESOURCE,
                        activity = activity,
                        group = 1,
                    )
                )

                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_settings_github),
                        content = "",
                        action = ACTION_GITHUB,
                        activity = activity,
                        group = 1,
                    )
                )
                add(
                    ItemSettingOptionsModel(
                        title = CommonUtils.getString(R.string.text_settings_clear),
                        content = "",
                        action = ACTION_CLEAR,
                        activity = activity,
                        group = 2,
                    )
                )
            }
        }
    }

    private class HrefBean(
        val title: String,
        val url: String,
    )

    private val hrefList = mutableListOf<HrefBean>()

    init {
        // 免费资源来源要求标注出处
        hrefList.clear()
        hrefList.add(HrefBean("icons by [Bing AI]", "https://www.bing.com/"))
        hrefList.add(HrefBean("icons by [Google Fonts]", "https://fonts.google.com/"))
        hrefList.add(HrefBean("images by [Pixabay]", "https://pixabay.com/"))
        hrefList.add(HrefBean("stickers by [Flaticon]", "https://www.flaticon.com/"))
    }

    override fun onBind(vh: BindingAdapter.BindingViewHolder) {
        val data = vh.adapter.models
        val temp = mutableListOf<ItemSettingOptionsModel>()
        data?.forEach {
            if (it is ItemSettingOptionsModel && it.group == this.group) {
                temp.add(it)
            }
        }
        DataBindingUtil.bind<ItemSettingOptionsBinding>(vh.itemView)?.let {
            if (temp.isNotEmpty()) {
                it.clRoot.updateLayoutParams<RecyclerView.LayoutParams> {
                    topMargin = 0
                }
                it.vDividing.isInvisible = false
                if (temp.size == 1) {
                    it.vDividing.isInvisible = true
                    it.clRoot.background = ResourcesCompat.getDrawable(
                        AppContext.application.resources,
                        R.drawable.shape_dialog_add_bg_white,
                        AppContext.application.theme
                    )
                    if (vh.modelPosition != 0) {
                        it.clRoot.updateLayoutParams<RecyclerView.LayoutParams> {
                            topMargin = GROUP_DIVIDING
                        }
                    }
                } else {
                    when (temp.indexOf(this)) {
                        0 -> {
                            if (vh.modelPosition != 0) {
                                it.clRoot.updateLayoutParams<RecyclerView.LayoutParams> {
                                    topMargin = GROUP_DIVIDING
                                }
                            }
                            it.clRoot.background = ResourcesCompat.getDrawable(
                                AppContext.application.resources,
                                R.drawable.shape_item_setting_options_bg_top,
                                AppContext.application.theme
                            )
                        }

                        temp.size - 1 -> {
                            it.clRoot.background = ResourcesCompat.getDrawable(
                                AppContext.application.resources,
                                R.drawable.shape_item_setting_options_bg_bottom,
                                AppContext.application.theme
                            )
                            it.vDividing.isInvisible = true
                        }

                        else -> {
                            it.clRoot.setBackgroundColor(
                                ResourcesCompat.getColor(
                                    AppContext.application.resources,
                                    R.color.white,
                                    AppContext.application.theme
                                )
                            )
                        }
                    }
                }
            }
            it.tvTitle.text = title
            it.tvContent.text = content
        }
    }

    fun onClick(fragment: Fragment, callback: () -> Unit) {
        when (action) {
            ACTION_CLEAR -> {
                XPopup.Builder(this.activity)
                    .asConfirm(CommonUtils.getString(R.string.text_settings_clear_confirm_tile),
                        CommonUtils.getString(R.string.text_settings_clear_confirm_subtitle),
                        {
                            DataUtils.clear(activity as AppCompatActivity)
                            CommonUtils.toast(R.string.text_settings_clear_success)
                        }, {

                        }).show()
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

            ACTION_CHANGE_NOTICE_TIME -> {
                XPopup.Builder(this.activity)
                    .hasStatusBarShadow(false)
                    .hasNavigationBar(false)
                    .isDestroyOnDismiss(true)
                    .autoOpenSoftInput(true)
                    .autoFocusEditText(true)
                    .setPopupCallback(object : SimpleCallback() {
                        override fun onCreated(popupView: BasePopupView?) {
                            super.onCreated(popupView)
                            if (popupView is InputConfirmPopupView) {
                                popupView.editText.inputType = InputType.TYPE_CLASS_NUMBER
                            }
                        }
                    })
                    .asInputConfirm(
                        CommonUtils.getString(R.string.text_settings_notice_time),
                        CommonUtils.getString(R.string.text_settings_notice_time_subtitle),
                        null,
                        CommonUtils.getString(R.string.text_settings_notice_time_hint)
                    ) {
                        if (!it.isDigitsOnly()) {
                            CommonUtils.toast(R.string.text_settings_notice_time_error_number)
                            return@asInputConfirm
                        }
                        val num = it.toInt()
                        if (num <= 0 || num > 365) {
                            CommonUtils.toast(R.string.text_settings_notice_time_error_number)
                            return@asInputConfirm
                        }
                        DataUtils.setNoticeTime(num)
                        CommonUtils.toast(R.string.text_settings_success)
                        callback.invoke()
                    }
                    .show()

            }

            ACTION_CHANGE_SHOW_MODEL -> {
                // 注意对应id的顺序
                val array = arrayOf(ShowModel.DEFAULT.text, ShowModel.GROUP.text)
                XPopup.Builder(this.activity)
                    .isDestroyOnDismiss(true)
                    .asCenterList(
                        CommonUtils.getString(R.string.text_settings_show_model_subtitle),
                        array,
                        null, DataUtils.getShowModel()
                    ) { position, _ ->
                        DataUtils.setShowModel(position)
                        CommonUtils.toast(R.string.text_settings_success)
                        callback.invoke()
                    }
                    .show()
            }

            ACTION_MANAGE_GROUPS -> {
                fragment.findNavController().navigate(
                    R.id.action_settingFragment_to_ManageGroupsFragment,
                    Bundle(),
                    CommonUtils.createNavOptions()
                )
            }

            ACTION_EMPTY -> {

            }
        }
    }

}