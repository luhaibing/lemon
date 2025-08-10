package com.mercer.glide.support

import android.content.Context
import androidx.core.net.toUri
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
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
 *   *.json 格式的动图资源
 */

class LottieDrawableDataFetcher(
    private val url: GlideUrl,
    private val context: Context,
    private val timeout: Duration = 30.seconds
) : DataFetcher<LottieDrawable> {

    companion object {

        fun convert(inputStream: InputStream, cacheKey: String?): LottieDrawable {
            val composition = LottieCompositionFactory.fromJsonInputStreamSync(inputStream, cacheKey).getValue()
            val drawable = LottieDrawable()
            drawable.setComposition(composition)
            return drawable
        }
    }

    private val delegate: DataFetcher<InputStream> by lazy {
        HttpUrlFetcher(url, timeout.inWholeMilliseconds.toInt())
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in LottieDrawable>) {
        val file = File(context.cacheDir, "${url.cacheKey.md5}.json")
        val drawable: LottieDrawable? = try {
            if (file.exists()) {
                convert(file.inputStream(), url.cacheKey)
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
                            callback.onDataReady(convert(buffer.inputStream(), url.cacheKey))
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

    override fun cleanup() {
        delegate.cleanup()
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun getDataClass(): Class<LottieDrawable> {
        return LottieDrawable::class.java
    }

    override fun getDataSource(): DataSource {
        // 因为 DataFetcher 意味着自己处理 文件的存储和解码,所以需要返回 LOCAL,且 diskCacheStrategy 需要设置为 NONE
        return DataSource.LOCAL
    }

}

/** String 链接*/
class String2LottieDrawableLoaderFactory(private val context: Context) : ModelLoaderFactory<String, LottieDrawable> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, LottieDrawable?> {
        return object : ModelLoader<String, LottieDrawable?> {

            override fun buildLoadData(model: String, width: Int, height: Int, options: Options): LoadData<LottieDrawable?>? {
                val url = model.toSupportedUrl()
                return LoadData(url, LottieDrawableDataFetcher(url, context))
            }

            override fun handles(model: String): Boolean {
                return model.toUri().path?.endsWith(".json", ignoreCase = true) ?: false
            }
        }
    }

    override fun teardown() {
        // Do nothing.
    }

}

/** GlideUrl 链接*/
class GlideUrl2LottieDrawableLoaderFactory(private val context: Context) : ModelLoaderFactory<GlideUrl, LottieDrawable> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl?, LottieDrawable?> {
        return object : ModelLoader<GlideUrl?, LottieDrawable?> {
            override fun buildLoadData(model: GlideUrl, width: Int, height: Int, options: Options): LoadData<LottieDrawable?>? {
                return LoadData(ObjectKey(model), LottieDrawableDataFetcher(model, context))
            }

            override fun handles(model: GlideUrl): Boolean {
                return model.toURL().path.endsWith(".json", ignoreCase = true)
            }
        }
    }

    override fun teardown() {
        // Do nothing.
    }

}