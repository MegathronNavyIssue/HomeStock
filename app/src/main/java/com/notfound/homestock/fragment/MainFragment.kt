package com.notfound.homestock.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.impl.ConfirmPopupView
import com.lxj.xpopup.interfaces.SimpleCallback
import com.notfound.homestock.R
import com.notfound.homestock.base.BaseFragment
import com.notfound.homestock.bean.ItemInfoBean
import com.notfound.homestock.bean.ShoppingCartBean
import com.notfound.homestock.databinding.FragmentMainBinding
import com.notfound.homestock.dialog.ObjectAddDialog
import com.notfound.homestock.dialog.ObjectEditDialog
import com.notfound.homestock.model.ItemExampleObjectModel
import com.notfound.homestock.model.ItemObjectModel
import com.notfound.homestock.utils.CommonUtils
import com.notfound.homestock.utils.CommonUtils.createNavOptions
import com.notfound.homestock.utils.DataUtils
import com.notfound.homestock.utils.SizeUtils
import com.notfound.homestock.view.PointConstraintLayout
import com.notfound.homestock.viewmodel.RvViewModel
import com.xuexiang.xui.widget.guidview.FocusShape
import com.xuexiang.xui.widget.guidview.GuideCaseQueue
import com.xuexiang.xui.widget.guidview.GuideCaseView


class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    private var rvViewModel: RvViewModel? = null
    override fun initView() {
        binding.rv.linear().setup {
            addType<ItemObjectModel>(R.layout.item_object_style_ii)
            addType<ItemExampleObjectModel>(R.layout.item_object_style_ii)

            R.id.cl_object_root.onLongClick {
                val textArray = arrayOf(
                    resources.getString(R.string.text_item_update),
                    resources.getString(R.string.text_item_delete),
                    resources.getString(R.string.text_item_add_shopping_cart),
                )
                when (val data = _data) {
                    is ItemObjectModel -> {
                        itemView.findViewById<PointConstraintLayout>(it)?.let { view ->
                            XPopup.Builder(requireActivity())
                                .hasShadowBg(false)
                                .isClickThrough(false)
                                .atPoint(view.pointF)
                                .borderRadius(SizeUtils.dp2px(6f).toFloat())
                                .asAttachList(textArray, null) { _, t ->
                                    if (activity is AppCompatActivity) {
                                        when (t) {
                                            // 更新
                                            resources.getString(R.string.text_item_update) -> {
                                                XPopup.Builder(context)
                                                    .asCustom(ObjectEditDialog(context).also {
                                                        it.itemInfoBean = data.itemInfoBean
                                                    }).show()
                                            }
                                            // 删除
                                            resources.getString(R.string.text_item_delete) -> {
                                                XPopup.Builder(requireActivity())
                                                    .hasShadowBg(false)
                                                    .isClickThrough(false)
                                                    .borderRadius(SizeUtils.dp2px(6f).toFloat())
                                                    .setPopupCallback(object : SimpleCallback() {
                                                        override fun onCreated(popupView: BasePopupView?) {
                                                            super.onCreated(popupView)
                                                            if (popupView is ConfirmPopupView) {
                                                                popupView.confirmTextView.setTextColor(
                                                                    Color.parseColor("#FF5656")
                                                                )
                                                            }
                                                        }
                                                    })
                                                    .asConfirm(
                                                        "",
                                                        String.format(
                                                            resources.getString(R.string.text_item_delete_confirm),
                                                            data.name
                                                        ), {
                                                            DataUtils.removeItemRefresh(
                                                                data.itemGroupPosition,
                                                                activity as AppCompatActivity
                                                            )
                                                        }, {
                                                            // do nothing
                                                        }
                                                    )
                                                    .show()

                                            }
                                            // 添加到备货清单
                                            CommonUtils.getString(R.string.text_item_add_shopping_cart) -> {
                                                DataUtils.addShoppingCartObject(
                                                    ShoppingCartBean(
                                                        id = System.currentTimeMillis(),
                                                        name = data.name
                                                    ),
                                                    activity as AppCompatActivity
                                                )
                                                CommonUtils.toast(R.string.text_item_add_shopping_cart_success)
                                            }
                                        }

                                    }
                                }.show()
                        }
                    }
                    // 样例禁止编辑
                    is ItemExampleObjectModel -> {
                        itemView.findViewById<PointConstraintLayout>(it)?.let { view ->
                            XPopup.Builder(requireActivity())
                                .hasShadowBg(false)
                                .isClickThrough(false)
                                .atPoint(view.pointF)
                                .borderRadius(SizeUtils.dp2px(6f).toFloat())
                                .asAttachList(textArray, null) { _, _ ->
                                    CommonUtils.toast(R.string.text_item_example_alert)
                                }.show()
                        }

                    }
                }
            }
        }

        binding.ivAdd.setOnClickListener {
            context?.let { ctx ->
                XPopup.Builder(ctx).asCustom(ObjectAddDialog(ctx)).show()
            }
        }

        binding.ivShoppingCart.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_ShoppingCartFragment,
                Bundle(),
                createNavOptions()
            )
        }


        binding.ivSetting.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_settingFragment,
                Bundle(),
                createNavOptions()
            )
        }

        if (activity is AppCompatActivity) {
            // liveData postValue时用的activity要和observe时的activity一致
            rvViewModel =
                ViewModelProvider(activity as AppCompatActivity)[RvViewModel::class.java].also {
                    it.itemInfoData.observe(this) { t ->
                        // 排序
                        val data = DataUtils.sortItemInfoBean(t)
                        val viewList = mutableListOf<Any>()
                        viewList.addAll(dataToView(data))
                        // 如果没有数据，则展示样例
                        if (viewList.isEmpty()) {
                            viewList.add(ItemExampleObjectModel(expirationTime = System.currentTimeMillis() - 3600 * 1000 * 24))
                            viewList.add(ItemExampleObjectModel(expirationTime = System.currentTimeMillis() + 3600 * 1000 * 24))
                            viewList.add(ItemExampleObjectModel(expirationTime = System.currentTimeMillis() + 86400000 * 60L))
                        }
                        binding.rv.bindingAdapter.setDifferModels(viewList)
                        // 引导页
                        // 这样实现引导页开销也太TM大了，但是我又懒得放一个静态的item上去
                        if (!DataUtils.loadGuide(DataUtils.GUIDE_HOME)) {
                            activity?.let { act ->
                                CommonUtils.showGuide(act, binding.rv) { p ->
                                    binding.rv.layoutManager?.let { layoutManager ->
                                        val view = layoutManager.findViewByPosition(p)
                                        GuideCaseQueue().apply {
                                            add(
                                                GuideCaseView.Builder(act)
                                                    .title(CommonUtils.getString(R.string.text_guide_add))
                                                    .adjustHeight(0)
                                                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                                                    .focusOn(binding.ivAdd)
                                                    .build()
                                            )
                                            add(
                                                GuideCaseView.Builder(act)
                                                    .title(CommonUtils.getString(R.string.text_guide_to_shopping_cart))
                                                    .adjustHeight(0)
                                                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                                                    .focusOn(binding.ivShoppingCart)
                                                    .build()
                                            )
                                            add(
                                                GuideCaseView.Builder(act)
                                                    .title(CommonUtils.getString(R.string.text_guide_item_info))
                                                    .adjustHeight(0)
                                                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                                                    .focusOn(view)
                                                    .build()
                                            )
                                            setCompleteListener {
                                                DataUtils.setGuide(DataUtils.GUIDE_HOME)
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
        rvViewModel?.itemInfoData?.postValue(DataUtils.loadItemInfo())
    }

    private fun dataToView(data: List<ItemInfoBean>): MutableList<ItemObjectModel> {
        val list = mutableListOf<ItemObjectModel>()
        for (obj in data) {
            list.add(
                ItemObjectModel(
                    name = obj.name,
                    num = obj.num,
                    editTime = obj.editTime,
                    expirationTime = obj.expirationTime,
                    itemInfoBean = obj,
                )
            )
        }
        return list
    }

}