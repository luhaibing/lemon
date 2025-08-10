package com.mercer.glide.support.decoders

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.mercer.glide.support.SVGADrawableDataFetcher
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import java.io.InputStream

/**
 * @author      Mercer
 * @Created     2025/08/10.
 * @Description:
 *   SVGADrawable 解码器
 */
class SVGADrawableADecoder(
    val context: Context
) : ResourceDecoder<InputStream, SVGADrawable> {

    private val parser = SVGAParser(context)

    override fun handles(source: InputStream, options: Options): Boolean {
        // TODO: 暂无方法检查  source 来源
        return true
    }

    override fun decode(
        source: InputStream,
        width: Int,
        height: Int,
        options: Options
    ): Resource<SVGADrawable?>? {
        return try {
            val drawable = SVGADrawableDataFetcher.convert(source, parser)
            SimpleResource(drawable)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}