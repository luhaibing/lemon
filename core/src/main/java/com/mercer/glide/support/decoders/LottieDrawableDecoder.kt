package com.mercer.glide.support.decoders

import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.mercer.glide.support.LottieDrawableDataFetcher
import java.io.InputStream

/**
 * @author      Mercer
 * @Created     2025/08/10.
 * @Description:
 *   LottieDrawable 解码器
 */
class LottieDrawableDecoder : ResourceDecoder<InputStream, LottieDrawable> {

    override fun handles(source: InputStream, options: Options): Boolean {
        // TODO: 暂无方法检查  source 来源
        return true
    }

    override fun decode(
        source: InputStream,
        width: Int,
        height: Int,
        options: Options
    ): Resource<LottieDrawable?>? {
        return try {
            val drawable = LottieDrawableDataFetcher.convert(source, null)
            SimpleResource(drawable)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}