package com.notfound.homestock.dialog

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
                        numMax =  filter.max
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
                        if (numMax>0 && num.toString().length <= numMax) {
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
                        group = "",
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
}