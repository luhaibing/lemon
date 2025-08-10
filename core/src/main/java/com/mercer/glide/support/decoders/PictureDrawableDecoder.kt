package com.mercer.glide.support.decoders

import android.graphics.drawable.PictureDrawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.mercer.glide.support.PictureDrawableDataFetcher
import java.io.InputStream

/**
 * @author      Mercer
 * @Created     2025/08/10.
 * @Description:
 *   PictureDrawable 解码器
 */
class PictureDrawableDecoder : ResourceDecoder<InputStream, PictureDrawable> {

    override fun handles(source: InputStream, options: Options): Boolean {
        // TODO: 暂无方法检查  source 来源
        return true
    }

    override fun decode(
        source: InputStream,
        width: Int,
        height: Int,
        options: Options
    ): Resource<PictureDrawable?>? {
        return try {
            val drawable = PictureDrawableDataFetcher.convert(source)
            SimpleResource(drawable)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}