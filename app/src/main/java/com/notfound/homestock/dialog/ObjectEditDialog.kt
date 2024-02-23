package com.notfound.homestock.dialog

import android.app.Activity
import android.content.Context
import android.text.InputFilter
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.CenterPopupView
import com.lxj.xpopupext.listener.TimePickerListener
import com.lxj.xpopupext.popup.TimePickerPopup
import com.notfound.homestock.MainActivity
import com.notfound.homestock.R
import com.notfound.homestock.bean.GroupBean
import com.notfound.homestock.databinding.DialogObjectEditBinding
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.utils.CommonUtils
import com.notfound.homestock.utils.DataUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ObjectEditDialog constructor(context: Context) : CenterPopupView(context) {
    var itemInfoBean: ItemInfoBean? = null

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_object_edit
    }

    private var objExpirationTime = 0L

    override fun onCreate() {
        DataBindingUtil.bind<DialogObjectEditBinding>(contentView)?.let { binding ->
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)

            itemInfoBean?.let { bean ->
                var numMax = -1
                val filters = binding.etNum.filters
                for (filter in filters) {
                    if (filter is InputFilter.LengthFilter) {
                        numMax = filter.max
                    }
                }

                binding.etName.setText(bean.name)
                binding.etNum.setText(bean.num.toString())
                if (bean.expirationTime != 0L) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                    binding.tvTime.text = dateFormat.format(bean.expirationTime)
                    objExpirationTime = bean.expirationTime
                }
                binding.ivUp.setOnClickListener {
                    val t = binding.etNum.text.toString()
                    if (t.isNotBlank() && t.isDigitsOnly()) {
                        val num = t.toInt() + 1
                        if (numMax > 0 && num.toString().length <= numMax) {
                            binding.etNum.setText(num.toString())
                        }
                    }
                }

                binding.ivDown.setOnClickListener {
                    val t = binding.etNum.text.toString()
                    if (t.isNotBlank() && t.isDigitsOnly()) {
                        val num = t.toInt() - 1
                        if (num > 0) {
                            binding.etNum.setText(num.toString())
                        }
                    }
                }

                binding.tvTime.setOnClickListener {
                    val popup = TimePickerPopup(context)
                        .setYearRange(currentYear, currentYear + 20)
                        .setTimePickerListener(object : TimePickerListener {
                            override fun onTimeChanged(date: Date?) {
                            }

                            override fun onTimeConfirm(date: Date, view: View?) {
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                                binding.tvTime.text = dateFormat.format(date)
                                objExpirationTime = date.time
                            }

                            override fun onCancel() {
                            }
                        })
                    XPopup.Builder(context).asCustom(popup).show()
                }


                binding.ivReject.setOnClickListener {
                    dismiss()
                }

                binding.tvGroup.apply {
                    DataUtils.getGroupData().apply {
                        // 设置未分组
                        add(
                            GroupBean(
                                id = GroupBean.GROUP_EMPTY_ID,
                                name = CommonUtils.getString(R.string.text_group_empty),
                                index = GroupBean.GROUP_INDEX_DEFAULT,
                                time = GroupBean.GROUP_INDEX_DEFAULT.toLong(),
                            )
                        )
                    }.find { it.id == bean.groupId }?.let { groupBean ->
                        text = groupBean.name
                    } ?: run {
                        text = CommonUtils.getString(R.string.text_group_empty)
                    }

                    setOnClickListener {
                        editGroup(this@ObjectEditDialog.activity, bean) {
                            binding.tvGroup.text = it.name
                            bean.groupId = it.id
                        }
                    }
                }


                binding.ivAccept.setOnClickListener {
                    val name = binding.etName.text.toString()
                    val num = binding.etNum.text.toString()
                    if (name.isBlank()) {
                        CommonUtils.toast(R.string.text_item_add_name_alert)
                        return@setOnClickListener
                    }
                    if (num.isBlank() || !num.isDigitsOnly() || num.toInt() <= 0) {
                        CommonUtils.toast(R.string.text_item_add_num_alert)
                        return@setOnClickListener
                    }
                    val itemInfo = ItemInfoBean(
                        id = bean.id,
                        name = name,
                        num = num.toInt(),
                        groupId = bean.groupId,
                        editTime = System.currentTimeMillis(),
                        expirationTime = objExpirationTime
                    )
                    if (activity is MainActivity) {
                        CommonUtils.toast(R.string.text_item_add_success)
                        DataUtils.updateItemRefresh(itemInfo, activity as AppCompatActivity)
                    }
                    dismiss()
                }
            } ?: kotlin.run {
                CommonUtils.toast(R.string.text_error)
                dismiss()
            }

        }
        super.onCreate()
    }

    private fun editGroup(
        activity: Activity,
        bean: ItemInfoBean,
        callback: (bean: GroupBean) -> Unit
    ) {
        val data = DataUtils.getGroupData()
        val array = mutableListOf<String>()
        // 新建分组按钮
        array.add(CommonUtils.getString(R.string.text_group_select_create))
        // 如果已经有分组了，则提供删除分组的功能
        if (bean.groupId != GroupBean.GROUP_EMPTY_ID) {
            array.add(CommonUtils.getString(R.string.text_group_select_delete))
        }
        // 已有分组
        data.forEach {
            if (it.id != GroupBean.GROUP_EMPTY_ID){
                array.add(it.name)
            }
        }
        // 展示已经分组
        XPopup.Builder(activity)
            .isDestroyOnDismiss(true)
            .isDarkTheme(true)
            .asCenterList(
                CommonUtils.getString(R.string.text_group_select_title),
                array.toTypedArray(),
                null
            ) { _, text ->
                when (text) {
                    // 如果点击的是新建分组,则展示新的弹窗输入分组
                    CommonUtils.getString(R.string.text_group_select_create) -> {
                        XPopup.Builder(activity)
                            .hasStatusBarShadow(false)
                            .hasNavigationBar(false)
                            .isDestroyOnDismiss(true)
                            .autoOpenSoftInput(true)
                            .isDarkTheme(true)
                            .asInputConfirm(
                                CommonUtils.getString(R.string.text_group_select_create),
                                "",
                                null,
                                ""
                            ) {
                                if (it.isBlank()) {
                                    CommonUtils.toast(R.string.text_group_select_create_error)
                                    return@asInputConfirm
                                }
                                val list = DataUtils.getGroupData()
                                val t = it.trim()
                                // 处理分组命名重复
                                val groupBean = list.find { obj ->
                                    obj.name == t || t == CommonUtils.getString(
                                        R.string.text_group_empty
                                    )
                                } ?: GroupBean(
                                    id = System.currentTimeMillis(),
                                    name = t,

                                ).also { bean ->
                                    list.add(bean)
                                    DataUtils.setGroupData(list)
                                }
                                callback.invoke(groupBean)
                            }.show()
                    }

                    CommonUtils.getString(R.string.text_group_select_delete) -> {
                        callback.invoke(
                            GroupBean(
                                id = GroupBean.GROUP_EMPTY_ID,
                                name = CommonUtils.getString(R.string.text_group_empty)
                            )
                        )
                    }

                    else -> {
                        DataUtils.getGroupData().find { obj -> obj.name == text }?.let {
                            callback.invoke(it)
                        } ?: kotlin.run {
                            CommonUtils.toast(R.string.text_error)
                        }
                    }
                }
            }.show()

    }

}