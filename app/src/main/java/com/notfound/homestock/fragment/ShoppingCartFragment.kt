package com.notfound.homestock.fragment

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ktx.immersionBar
import com.lxj.xpopup.XPopup
import com.notfound.homestock.R
import com.notfound.homestock.base.BaseFragment
import com.notfound.homestock.bean.ShoppingCartBean
import com.notfound.homestock.databinding.FragmentShoppingCartBinding
import com.notfound.homestock.dialog.ShoppingCartAddDialog
import com.notfound.homestock.model.ItemShoppingCartModel
import com.notfound.homestock.utils.CommonUtils
import com.notfound.homestock.utils.DataUtils
import com.notfound.homestock.viewmodel.RvViewModel
import com.xuexiang.xui.widget.guidview.FocusShape
import com.xuexiang.xui.widget.guidview.GuideCaseQueue
import com.xuexiang.xui.widget.guidview.GuideCaseView
import timber.log.Timber

class ShoppingCartFragment :
    BaseFragment<FragmentShoppingCartBinding>(R.layout.fragment_shopping_cart) {
    private var rvViewModel: RvViewModel? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        immersionBar {
            statusBarDarkFont(false)
        }
    }

    override fun initView() {
        binding.rv.linear().setup {
            addType<ItemShoppingCartModel>(R.layout.item_shopping_cart_object)
            R.id.checkbox.onClick {
                if (_data is ItemShoppingCartModel) {
                    val model = getModel<ItemShoppingCartModel>(adapterPosition)
                    model.isChecked = !model.isChecked
                    notifyItemChanged(adapterPosition)
                }
            }
            itemDifferCallback = object : ItemDifferCallback {
                override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                    if (oldItem is ItemShoppingCartModel && newItem is ItemShoppingCartModel) {
                        return oldItem.bean.id == newItem.bean.id
                    }
                    return super.areItemsTheSame(oldItem, newItem)
                }

                override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                    if (oldItem is ItemShoppingCartModel && newItem is ItemShoppingCartModel) {
                        return (oldItem.bean.id == newItem.bean.id) && (oldItem.isChecked == newItem.isChecked)
                    }
                    return super.areContentsTheSame(oldItem, newItem)
                }

                override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
                    if (oldItem is ItemShoppingCartModel && newItem is ItemShoppingCartModel) {
                        newItem.isChecked = oldItem.isChecked
                        return newItem
                    }
                    return super.getChangePayload(oldItem, newItem)
                }
            }
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        activity?.let { ctx ->
            binding.ivAdd.setOnClickListener {
                val temp = binding.rv.bindingAdapter.models
                Timber.e(temp?.toString())
                XPopup.Builder(ctx).asCustom(ShoppingCartAddDialog(ctx)).show()
            }
        }
        binding.ivDone.setOnClickListener {
            binding.rv.bindingAdapter.models?.let {
                if (activity is AppCompatActivity) {
                    val temp = mutableListOf<ShoppingCartBean>()
                    it.forEach {
                        it?.takeIf { it is ItemShoppingCartModel }?.let {
                            it as ItemShoppingCartModel
                            if (!it.isChecked) {
                                temp.add(it.bean)
                            }
                        }
                    }
                    DataUtils.updateShoppingCartObjectList(temp, activity as AppCompatActivity)
                    CommonUtils.toast(R.string.text_shopping_cart_update_success)
                }
            }
        }
        if (activity is AppCompatActivity) {
            rvViewModel =
                ViewModelProvider(activity as AppCompatActivity)[RvViewModel::class.java].also {
                    it.shoppingCartData.observe(this) { t ->
                        binding.rv.bindingAdapter.setDifferModels(t.toMutableList().apply {
                            if (isEmpty()) {
                                add(ShoppingCartBean(id = 0, name = "这是样例"))
                            }
                        }.map {
                            ItemShoppingCartModel.buildModel(it)
                        })

                        if (!DataUtils.loadGuide(DataUtils.GUIDE_SHOPPING_CART)) {
                            activity?.let { act ->
                                CommonUtils.showGuide(act, binding.rv) { p ->
                                    binding.rv.layoutManager?.let { layoutManager ->
                                        val view = layoutManager.findViewByPosition(p)
                                        GuideCaseQueue().apply {
                                            add(
                                                GuideCaseView.Builder(act)
                                                    .title(CommonUtils.getString(R.string.text_guide_shopping_cart_add))
                                                    .backgroundColor(Color.parseColor("#80ACACAC"))
                                                    .adjustHeight(0)
                                                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                                                    .focusOn(binding.ivAdd)
                                                    .build()
                                            )
                                            add(
                                                GuideCaseView.Builder(act)
                                                    .title(CommonUtils.getString(R.string.text_guide_shopping_cart_object))
                                                    .backgroundColor(Color.parseColor("#80ACACAC"))
                                                    .adjustHeight(0)
                                                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                                                    .focusOn(view)
                                                    .build()
                                            )
                                            add(
                                                GuideCaseView.Builder(act)
                                                    .title(CommonUtils.getString(R.string.text_guide_shopping_cart_done))
                                                    .backgroundColor(Color.parseColor("#80ACACAC"))
                                                    .adjustHeight(0)
                                                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                                                    .focusOn(binding.ivDone)
                                                    .build()
                                            )
                                            setCompleteListener {
                                                DataUtils.setGuide(DataUtils.GUIDE_SHOPPING_CART)
                                            }
                                        }.show()
                                    }
                                }
                            }
                        }
                    }
                }

        }
    }

    override fun initData() {
        rvViewModel?.shoppingCartData?.postValue(DataUtils.loadShoppingCartObject())
    }

}