package com.mercer.glide.support

import android.content.Context
import android.graphics.drawable.PictureDrawable
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.LibraryGlideModule
import com.mercer.glide.support.decoders.LottieDrawableDecoder
import com.mercer.glide.support.decoders.PictureDrawableDecoder
import com.mercer.glide.support.decoders.SVGADrawableADecoder
import com.opensource.svgaplayer.SVGADrawable
import java.io.InputStream

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
        // 方案一 : 自定义 ModelLoaderFactory
        /*
        registry.append(String::class.java, PictureDrawable::class.java, String2PictureDrawableLoaderFactory(context))
        registry.append(GlideUrl::class.java, PictureDrawable::class.java, GlideUrl2PictureDrawableLoaderFactory(context))
        registry.append(String::class.java, SVGADrawable::class.java, String2SVGADrawableLoaderFactory(context))
        registry.append(GlideUrl::class.java, SVGADrawable::class.java, GlideUrl2SVGADrawableLoaderFactory(context))
        registry.append(String::class.java, LottieDrawable::class.java, String2LottieDrawableLoaderFactory(context))
        registry.append(GlideUrl::class.java, LottieDrawable::class.java, GlideUrl2LottieDrawableLoaderFactory(context))
        */
        // 方案二 : 自定义解码器, 文件资源的下载完全由 Glide 处理, 解码由自己实现
        registry.append(InputStream::class.java, SVGADrawable::class.java, SVGADrawableADecoder(context))
        registry.append(InputStream::class.java, PictureDrawable::class.java, PictureDrawableDecoder())
        registry.append(InputStream::class.java, LottieDrawable::class.java, LottieDrawableDecoder())
    }

}