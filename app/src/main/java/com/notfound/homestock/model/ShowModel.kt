package com.notfound.homestock.model

import com.notfound.homestock.R
import com.notfound.homestock.utils.CommonUtils

enum class ShowModel(val id: Int, val text: String) {
    DEFAULT(0, CommonUtils.getString(R.string.text_settings_show_model_default)),
    GROUP(1, CommonUtils.getString(R.string.text_settings_show_model_group)),
}