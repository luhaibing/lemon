package com.mercer.glide.support.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.toRectF
import kotlin.also
import kotlin.apply
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @Created on 2024/11/14
 * @author     Mercer
 * @Description:
 *      图片加载中
 */
class LoadingDrawable(
    target: View,
    private val color: Int,
    private val radius: Float
) : Drawable(), Animatable, Drawable.Callback {

    init {
        target.post {
            val rect = Rect(0, 0, target.width, target.height)
            setBounds(rect)
            start()
        }
    }

    override fun draw(canvas: Canvas) {
        paint.shader = null
        paint.color = color
        canvas.drawRoundRect(bounds.toRectF(), radius, radius, paint)
        paint.shader = shader
        canvas.drawRoundRect(bounds.toRectF(), radius, radius, paint)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    private var valueAnimator: ValueAnimator? = null
    private var shader: LinearGradient? = null

    private val offsetMatrix: Matrix by lazy {
        Matrix()
    }

    override fun start() {
        if (isRunning) {
            return
        }
        val length = sqrt((bounds.width().toDouble().pow(2) + bounds.height().toDouble().pow(2))).toInt()
        ValueAnimator.ofInt(length, -length)
            .apply {
                addUpdateListener {
                    val offset = it.animatedValue as Int
                    offsetMatrix.setTranslate(offset.toFloat(), 0F)
                    shader?.setLocalMatrix(offsetMatrix)
                    invalidateSelf()
                }
                duration = 1 * 1000
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        start()
                    }
                })
            }
            .also {
                valueAnimator = it
            }.start()
    }

    override fun stop() {
        valueAnimator?.cancel()
    }

    override fun isRunning(): Boolean {
        return valueAnimator?.isRunning ?: false
    }

    ///////////////////////////////////////////////////////////////////////

    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun setBounds(bounds: Rect) {
        super.setBounds(bounds)
        val size = min(bounds.width(), bounds.height())
        shader = LinearGradient(
            0F, size.toFloat(),
            size.toFloat(), 0F,
            intArrayOf(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
                Color.WHITE,
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            ), null, Shader.TileMode.CLAMP
        )
        offsetMatrix.reset()
    }

    ///////////////////////////////////////////////////////////////////////

    override fun invalidateDrawable(who: Drawable) {
        invalidateSelf()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        start()
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        stop()
    }

}