package com.notfound.homestock.fragment

import android.content.Context
import androidx.navigation.fragment.findNavController
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ktx.immersionBar
import com.notfound.homestock.R
import com.notfound.homestock.base.BaseFragment
import com.notfound.homestock.databinding.FragmentSettingBinding
import com.notfound.homestock.model.ItemSettingOptionsModel

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        immersionBar {
            statusBarDarkFont(true)
        }
    }

    override fun onDetach() {
        super.onDetach()
        immersionBar {
            statusBarDarkFont(false)
        }
    }

    override fun initView() {
        binding.rv.linear().setup {
            addType<ItemSettingOptionsModel>(R.layout.item_setting_options)
            R.id.cl_root.onClick {
                when (val data = _data) {
                    is ItemSettingOptionsModel -> {
                        data.onClick(this@SettingFragment) {
                            // 修改设置后刷新UI,可以用liveData,但是又好像没有必要
                            initData()
                        }
                    }
                }
            }
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun initData() {
        activity?.let {
            binding.rv.bindingAdapter.models = ItemSettingOptionsModel.getSettings(it)
        }
    }

}