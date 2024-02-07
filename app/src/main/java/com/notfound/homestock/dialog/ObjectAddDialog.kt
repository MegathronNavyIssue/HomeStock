package com.notfound.homestock.dialog

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
import com.notfound.homestock.databinding.DialogObjectAddBinding
import com.notfound.homestock.bean.ItemInfoBean
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
                    group = "",
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
}