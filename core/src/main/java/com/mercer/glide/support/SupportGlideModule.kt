package com.mercer.glide.support

import android.content.Context
import android.graphics.drawable.PictureDrawable
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import com.opensource.svgaplayer.SVGADrawable

/**
 * @author      Mercer
 * @Created     2025/08/10.
 * @Description:
 *   Glide 支持
 */
@GlideModule
class SupportGlideModule : LibraryGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        e("SupportGlideModule 注册组件.")
        // 方案一
        registry.append(String::class.java, PictureDrawable::class.java, String2PictureDrawableLoaderFactory(context))
        registry.append(GlideUrl::class.java, PictureDrawable::class.java, GlideUrl2PictureDrawableLoaderFactory(context))
        registry.append(String::class.java, SVGADrawable::class.java, String2SVGADrawableLoaderFactory(context))
        registry.append(GlideUrl::class.java, SVGADrawable::class.java, GlideUrl2SVGADrawableLoaderFactory(context))
        registry.append(String::class.java, LottieDrawable::class.java, String2LottieDrawableLoaderFactory(context))
        registry.append(GlideUrl::class.java, LottieDrawable::class.java, GlideUrl2LottieDrawableLoaderFactory(context))
    }

}