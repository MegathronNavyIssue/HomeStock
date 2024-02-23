package com.notfound.homestock.fragment

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ktx.immersionBar
import com.lxj.xpopup.XPopup
import com.notfound.homestock.R
import com.notfound.homestock.base.BaseFragment
import com.notfound.homestock.bean.GroupBean
import com.notfound.homestock.databinding.FragmentManageGroupsBinding
import com.notfound.homestock.model.ItemGroupModel
import com.notfound.homestock.utils.CommonUtils
import com.notfound.homestock.utils.DataUtils
import com.notfound.homestock.utils.SizeUtils
import com.notfound.homestock.view.PointConstraintLayout
import com.xuexiang.xui.widget.guidview.FocusShape
import com.xuexiang.xui.widget.guidview.GuideCaseQueue
import com.xuexiang.xui.widget.guidview.GuideCaseView

class ManageGroupsFragment :
    BaseFragment<FragmentManageGroupsBinding>(R.layout.fragment_manage_groups) {

    // 保存被删除的分组ID，用于重置关联的子项
    private val resetId = mutableListOf<Long>()
    private var guideCaseQueue: GuideCaseQueue? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        immersionBar {
            statusBarDarkFont(true)
        }
    }

    override fun initView() {
        binding.rv.linear().setup {
            addType<ItemGroupModel>(R.layout.item_group)
            R.id.cl_group_root.onClick {
                val data = _data as ItemGroupModel
                if (data.bean.id == GroupBean.GROUP_EMPTY_ID) {
                    CommonUtils.toast(R.string.text_group_edit_error)
                    return@onClick
                }
                val textArray = arrayOf(
                    resources.getString(R.string.text_group_edit_name),
                    resources.getString(R.string.text_item_delete),
                )
                itemView.findViewById<PointConstraintLayout>(it)?.let { view ->
                    XPopup.Builder(requireActivity())
                        .hasShadowBg(false)
                        .isClickThrough(false)
                        .atPoint(view.pointF)
                        .borderRadius(SizeUtils.dp2px(6f).toFloat())
                        .asAttachList(textArray, null) { _, t ->
                            if (activity is AppCompatActivity) {
                                when (t) {
                                    // 重命名
                                    resources.getString(R.string.text_group_edit_name) -> {
                                        XPopup.Builder(context)
                                            .asInputConfirm(
                                                resources.getString(R.string.text_group_edit_name),
                                                "",
                                                null,
                                                ""
                                            ) { str ->
                                                val text = str.trim()
                                                models?.find { obj -> obj is ItemGroupModel && obj.bean.name == text }
                                                    ?.let {
                                                        CommonUtils.toast(R.string.text_group_edit_name_error)
                                                    } ?: kotlin.run {
                                                    data.bean.name = text
                                                    notifyItemChanged(layoutPosition)
                                                }
                                            }.show()
                                    }
                                    // 删除
                                    resources.getString(R.string.text_item_delete) -> {
                                        (models as? MutableList)?.removeAt(layoutPosition)?.apply {
                                            resetId.add(data.bean.id)
                                        }
                                        notifyItemRemoved(layoutPosition)
                                    }
                                }
                            }
                        }.show()
                }
            }
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivDone.setOnClickListener {
            val temp = mutableListOf<GroupBean>()
            binding.rv.bindingAdapter.models?.forEachIndexed { index, any ->
                if (any is ItemGroupModel) {
                    temp.add(any.bean.apply {
                        this.index = index
                    })
                }
            }
            resetId.add(0)
            if (resetId.isNotEmpty()) {
                val list = DataUtils.getItemData()
                list.forEach {
                    if (resetId.contains(it.groupId)) {
                        it.groupId = GroupBean.GROUP_EMPTY_ID
                    }
                }
                DataUtils.setItemData(list)
            }
            DataUtils.setGroupData(temp)
            CommonUtils.toast(R.string.text_item_add_success)
            findNavController().popBackStack()
        }

        if (!DataUtils.loadGuide(DataUtils.GUIDE_MANAGE_GROUP)) {
            activity?.let { act ->
                CommonUtils.showGuide(binding.rv) { p ->
                    binding.rv.layoutManager?.let { layoutManager ->
                        val view = layoutManager.findViewByPosition(p)
                        guideCaseQueue = GuideCaseQueue().apply {
                            add(
                                GuideCaseView.Builder(act)
                                    .title(CommonUtils.getString(R.string.text_guide_manager_group_drag))
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
                                DataUtils.setGuide(DataUtils.GUIDE_MANAGE_GROUP)
                                guideCaseQueue = null
                            }
                            show()
                        }
                    }
                }
            }
        }
    }

    override fun initData() {
        binding.rv.bindingAdapter.models = ItemGroupModel.buildModel()
    }

}