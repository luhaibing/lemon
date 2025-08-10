package com.mercer.glide.support

import android.content.Context
import android.graphics.drawable.PictureDrawable
import androidx.core.net.toUri
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.data.HttpUrlFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.caverock.androidsvg.SVG
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.InputStream
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * @author      Mercer
 * @Created     2025/08/10.
 * @Description:
 *   加载 *.svg 格式的图片资源,自定义实现缓存到本地
 */

/** 数据获取 */
class PictureDrawableDataFetcher(
    private val url: GlideUrl,
    private val context: Context,
    private val timeout: Duration = 30.seconds
) : DataFetcher<PictureDrawable> {

    private val delegate: DataFetcher<InputStream> by lazy {
        HttpUrlFetcher(url, timeout.inWholeMilliseconds.toInt())
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in PictureDrawable>) {
        val file = File(context.cacheDir, "${url.cacheKey.md5}.svg")
        val drawable: PictureDrawable? = try {
            if (file.exists()) {
                convert(file.inputStream())
            } else {
                null
            }
        } catch (e: Exception) {
            e("[$file]文件存在", "解码失败", e)
            e.printStackTrace()
            null
        }
        if (drawable != null) {
            callback.onDataReady(drawable)
        } else {
            file.delete()
            delegate.loadData(priority, object : DataFetcher.DataCallback<InputStream> {
                override fun onDataReady(data: InputStream?) {
                    if (data == null) {
                        callback.onLoadFailed(Exception("数据流为空,无法解析."))
                    } else {
                        file.deleteOnExit()
                        try {
                            val buffer = data.source().buffer()
                            val sink = file.sink().buffer()
                            // 写入文件并缓存到内存
                            // val pipe = okio.Pipe(100 * 1024 * 1024)
                            val peeked = buffer.peek()
                            sink.writeAll(peeked)   // 写入文件
                            sink.flush()
                            sink.close()
                            callback.onDataReady(convert(buffer.inputStream()))
                        } catch (e: Exception) {
                            e("[$file]文件保存失败", e)
                            e.printStackTrace()
                        }
                    }
                }

                override fun onLoadFailed(e: Exception) {
                    callback.onLoadFailed(e)
                }
            })
        }
    }

    private fun convert(inputStream: InputStream): PictureDrawable {
        val svg: SVG = SVG.getFromInputStream(inputStream)
        val picture = svg.renderToPicture()
        return PictureDrawable(picture)
    }

    override fun cleanup() {
        delegate.cleanup()
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun getDataClass(): Class<PictureDrawable> {
        return PictureDrawable::class.java
    }

    override fun getDataSource(): DataSource {
        // 因为 DataFetcher 意味着自己处理 文件的存储和解码,所以需要返回 LOCAL,且 diskCacheStrategy 需要设置为 NONE
        return DataSource.LOCAL
    }

}

/** String 链接*/
class String2PictureDrawableLoaderFactory(private val context: Context) : ModelLoaderFactory<String, PictureDrawable> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, PictureDrawable?> {
        return object : ModelLoader<String, PictureDrawable?> {

            override fun buildLoadData(model: String, width: Int, height: Int, options: Options): LoadData<PictureDrawable?>? {
                val url = model.toSupportedUrl()
                return LoadData(url, PictureDrawableDataFetcher(url, context))
            }

            override fun handles(model: String): Boolean {
                return model.toUri().path?.endsWith(".svg", ignoreCase = true) ?: false
            }
        }
    }

    override fun teardown() {
        // Do nothing.
    }

}

/** GlideUrl 链接*/
class GlideUrl2PictureDrawableLoaderFactory(private val context: Context) : ModelLoaderFactory<GlideUrl, PictureDrawable> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl?, PictureDrawable?> {
        return object : ModelLoader<GlideUrl?, PictureDrawable?> {
            override fun buildLoadData(model: GlideUrl, width: Int, height: Int, options: Options): LoadData<PictureDrawable?>? {
                return LoadData(ObjectKey(model), PictureDrawableDataFetcher(model, context))
            }

            override fun handles(model: GlideUrl): Boolean {
                return model.toURL().path.endsWith(".svg", ignoreCase = true)
            }
        }
    }

    override fun teardown() {
        // Do nothing.
    }

}