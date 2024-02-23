package com.notfound.homestock.dialog

import android.app.Activity
import android.content.Context
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
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.databinding.DialogObjectAddBinding
import com.notfound.homestock.utils.CommonUtils
import com.notfound.homestock.utils.DataUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ObjectAddDialog(context: Context) : CenterPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_object_add
    }

    private var objExpirationTime = 0L

    override fun onCreate() {
        var groupBean :GroupBean? = null

        DataBindingUtil.bind<DialogObjectAddBinding>(contentView)?.let { binding ->
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)

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

            binding.tvGroup.setOnClickListener {
                editGroup(this.activity){
                    binding.tvGroup.text = it.name
                    groupBean  = it
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
                    id = System.currentTimeMillis(),
                    name = name,
                    num = num.toInt(),
                    groupId = groupBean?.id?:GroupBean.GROUP_EMPTY_ID,
                    editTime = System.currentTimeMillis(),
                    expirationTime = objExpirationTime
                )
                if (activity is MainActivity) {
                    CommonUtils.toast(R.string.text_item_add_success)
                    DataUtils.addItemRefresh(itemInfo, activity as AppCompatActivity)
                }
                dismiss()
            }
        }
        super.onCreate()
    }

   private fun editGroup(activity: Activity, callback: (bean: GroupBean) -> Unit) {
        val data = DataUtils.getGroupData()
        val array = mutableListOf<String>()
        // 新建分组按钮
        array.add(CommonUtils.getString(R.string.text_group_select_create))
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
                                if (it.isBlank()){
                                    CommonUtils.toast(R.string.text_group_select_create_error)
                                    return@asInputConfirm
                                }
                                val list = DataUtils.getGroupData()
                                val t = it.trim()
                                if (t == CommonUtils.getString(R.string.text_group_empty)){
                                    return@asInputConfirm
                                }
                                // 处理分组命名重复
                                val groupBean = list.find { obj -> obj.name == t} ?: GroupBean(
                                    id = System.currentTimeMillis(),
                                    name = t
                                ).also { bean ->
                                    list.add(bean)
                                    DataUtils.setGroupData(list)
                                }
                                callback.invoke(groupBean)
                            }.show()
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