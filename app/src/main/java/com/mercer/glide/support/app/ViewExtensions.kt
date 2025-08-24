package com.mercer.glide.support.app

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toDrawable

/**
 * @author      Mercer
 * @Created     2025/08/24.
 * @Description:
 *   View 扩展
 */
/**
 * 为控件添加在上 按下的 点击背景
 * @param colorRes 按下时的阴影色
 */
fun View.withPressedShadowColor(@ColorRes colorRes: Int) {
    withPressedShadowColorInt(ContextCompat.getColor(context, colorRes))
}

/**
 * 为控件添加在上 按下的 点击背景
 * @param colorInt 按下时的阴影色
 */
fun View.withPressedShadowColorInt(@ColorInt colorInt: Int) {
    val drawable = background
    val stateList = backgroundTintList
    if (stateList != null) {
        val attrs = arrayOf(
            // 基本状态
            android.R.attr.state_pressed,      // 按下状态
            android.R.attr.state_focused,       // 获得焦点
            android.R.attr.state_selected,       // 选中状态
            android.R.attr.state_activated,       // 激活状态
            android.R.attr.state_enabled,        // 启用状态
            android.R.attr.state_hovered,         // 悬停状态（API 14+）
            // 可选状态
            android.R.attr.state_checked,       // 选中状态
            android.R.attr.state_checkable,      // 可选中状态
            android.R.attr.state_active,         // 活动状态
            android.R.attr.state_single,         // 单一样式
            android.R.attr.state_first,          // 第一个项目
            android.R.attr.state_middle,          // 中间项目
            android.R.attr.state_last,            // 最后一个项目
            // 编辑状态
            android.R.attr.state_multiline,     // 多行状态
            android.R.attr.state_expanded,        // 展开状态 )
            // android.R.attr.state_editabled,      // 可编辑状态
            // android.R.attr.state_collapsed        // 折叠状态
        )
        val defaultColor = stateList.defaultColor
        val colors = Array(attrs.size) {
            val attr = attrs[it]
            if (attr == android.R.attr.state_pressed) {
                colorInt
            } else {
                stateList.getColorForState(intArrayOf(attr), defaultColor)
            }
        }
        val states = Array(attrs.size) {
            intArrayOf(attrs[it])
        }
        if (drawable == null) {
            // 当没有 drawable 时,只设置 tint 是无效果的,所以在 state_pressed 下设置一个 background, 暂不考虑 state_pressed 时drawable 不可见的情况
            val stateListDrawable = StateListDrawable()
            stateListDrawable.addState(intArrayOf(0), null)
            stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), Color.BLACK.toDrawable())
            background = stateListDrawable
        }
        backgroundTintList = ColorStateList(arrayOf(*states, intArrayOf(0)), intArrayOf(*colors.toIntArray(), stateList.defaultColor))
    } else {
        val onGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // 获取正常状态下的 Drawable,即默认图层
                val defaultDrawable = if (drawable == null) {
                    null
                } else if (drawable is StateListDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val index = drawable.findStateDrawableIndex(intArrayOf())
                    drawable.getStateDrawable(index) ?: drawable.current
                } else {
                    drawable.current
                }
                val bounds = Rect(0, 0, width, height)
                defaultDrawable?.bounds = bounds
                // 绘制按下的阴影图层
                // 创建一个只有有透明度通道的位图
                val shadowBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8, true)
                val canvas = Canvas(shadowBitmap)
                val pressedDrawable = (if (defaultDrawable == null) {
                    // 将颜色的透明度设置为255后进行绘制
                    canvas.drawColor(ColorUtils.setAlphaComponent(colorInt, 0xFF))
                    shadowBitmap.toDrawable(resources)
                } else {
                    defaultDrawable.draw(canvas)
                    shadowBitmap.toDrawable(resources)
                }).apply {
                    alpha = 0xFF
                    setTint(colorInt)
                }
                // 根据默认 默认图层和阴影图层 拼接成 按下状态的图层
                val layerDrawable = LayerDrawable(arrayOf(defaultDrawable, pressedDrawable).filterNotNull().toTypedArray())
                // 根据原有 drawable 创建和替换掉原有的 background
                val stateListDrawable = StateListDrawable()
                stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), layerDrawable)
                // 填充原有的 drawable,如果 drawable 为 StateListDrawable时,原有效果仍会生效
                stateListDrawable.addState(intArrayOf(0), drawable)
                background = stateListDrawable
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }
}



