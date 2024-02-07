package com.notfound.homestock.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * @Description
 */
abstract class BaseFragment<B : ViewDataBinding>(@LayoutRes contentLayoutId: Int = 0) :
    Fragment(contentLayoutId), View.OnClickListener {

    val binding: B
        get() {
            return DataBindingUtil.bind(requireView())!!
        }

    protected abstract fun initView()
    protected abstract fun initData()

    override fun onClick(v: View) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        DataBindingUtil.bind<B>(view)
        try {
            initView()
            initData()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    @Deprecated(
        "建议使用onBackPressedDispatcher",
        ReplaceWith("requireActivity().onBackPressedDispatcher")
    )
    open fun onBackPressed(): Boolean {
        return false
    }
}