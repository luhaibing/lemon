package com.mercer.glide.support.app

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition

/**
 * @author      Mercer
 * @Created     2025/08/10.
 * @Description:
 *   简单的 ImageViewTarget
 */
class SimpleImageViewTarget<V : ImageView, T : Drawable>(
    view: V, val tag: String,
    val predicate: V.(T?) -> Unit = {},
) : ImageViewTarget<T>(view) {

    override fun setResource(resource: T?) {
        resource ?: return
        Log.e("TAG", "******************* $tag 加载成功 *******************")
        @Suppress("UNCHECKED_CAST")
        setDrawable(resource)
        predicate((view as V), resource)
    }

    override fun onLoadStarted(placeholder: Drawable?) {
        super.onLoadStarted(placeholder)
        Log.e("TAG", "******************* $tag 开始加载 *******************")
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        Log.e("TAG", "******************* $tag 加载失败 *******************")
    }

    override fun getCurrentDrawable(): Drawable? {
        return super.getCurrentDrawable()
    }

    override fun setDrawable(drawable: Drawable?) {
        super.setDrawable(drawable)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        super.onLoadCleared(placeholder)
    }

    override fun onResourceReady(resource: T, transition: Transition<in T>?) {
        super.onResourceReady(resource, transition)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

}