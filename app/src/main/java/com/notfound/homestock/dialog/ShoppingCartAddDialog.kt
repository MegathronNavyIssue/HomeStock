package com.notfound.homestock.dialog

import android.content.Context
import androidx.databinding.DataBindingUtil
import com.lxj.xpopup.core.CenterPopupView
import com.notfound.homestock.R
import com.notfound.homestock.bean.ShoppingCartBean
import com.notfound.homestock.databinding.DialogShoppingCartAddBinding
import com.notfound.homestock.utils.CommonUtils

class ShoppingCartAddDialog(context: Context,private val success: (bean: ShoppingCartBean) -> Unit) :
    CenterPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_shopping_cart_add
    }

    override fun onCreate() {
        DataBindingUtil.bind<DialogShoppingCartAddBinding>(contentView)?.let { binding ->

            binding.ivReject.setOnClickListener {
                dismiss()
            }

            binding.ivAccept.setOnClickListener {
                val name = binding.etName.text.toString()
                if (name.isBlank()) {
                    CommonUtils.toast(R.string.text_item_add_name_alert)
                    return@setOnClickListener
                }
                success.invoke(ShoppingCartBean(
                    id = System.currentTimeMillis(),
                    name = name,
                ))
                dismiss()
            }
        }

        super.onCreate()
    }
}