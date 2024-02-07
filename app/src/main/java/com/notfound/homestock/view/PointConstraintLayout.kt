package com.notfound.homestock.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout

/**
 *
 * @Describe : 返回点击view坐标点的 PointConstraintLayout
 */
@SuppressLint("ClickableViewAccessibility")
class PointConstraintLayout(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet, 0) {
    val pointF = PointF(0f, 0f)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                pointF.set(it.rawX, it.rawY)
            }
        }
        return super.onTouchEvent(event)
    }


}