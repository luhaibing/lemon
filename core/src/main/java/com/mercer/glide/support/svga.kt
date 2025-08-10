package com.mercer.glide.support

import android.content.Context
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
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAParser.ParseCompletion
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
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
 *   加载 *.svga 格式的动图资源,自定义实现缓存到本地
 */

/** 数据获取 */
class SVGADrawableDataFetcher(
    private val url: GlideUrl,
    private val context: Context,
    private val timeout: Duration = 30.seconds
) : DataFetcher<SVGADrawable> {

    private val parser: SVGAParser by lazy {
        SVGAParser(context)
    }

    private val delegate: DataFetcher<InputStream> by lazy {
        HttpUrlFetcher(url, timeout.inWholeMilliseconds.toInt())
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in SVGADrawable>) {
        val file = File(context.cacheDir, "${url.cacheKey.md5}.svga")
        val drawable: SVGADrawable? = try {
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

    private fun convert(inputStream: InputStream): SVGADrawable = runBlocking {
        val deferred = CompletableDeferred<SVGADrawable>()
        parser.decodeFromInputStream(inputStream, System.currentTimeMillis().toString(), object : ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                deferred.complete(SVGADrawable(videoItem))
            }

            override fun onError() {
                deferred.completeExceptionally(Exception("SVG解析失败"))
            }
        })
        deferred.await()
    }

    override fun cleanup() {
        delegate.cleanup()
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun getDataClass(): Class<SVGADrawable> {
        return SVGADrawable::class.java
    }

    override fun getDataSource(): DataSource {
        // 因为 DataFetcher 意味着自己处理 文件的存储和解码,所以需要返回 LOCAL,且 diskCacheStrategy 需要设置为 NONE
        return DataSource.LOCAL
    }

}

/** String 链接*/
class String2SVGADrawableLoaderFactory(private val context: Context) : ModelLoaderFactory<String, SVGADrawable> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, SVGADrawable?> {
        return object : ModelLoader<String, SVGADrawable?> {

            override fun buildLoadData(model: String, width: Int, height: Int, options: Options): LoadData<SVGADrawable?>? {
                val url = model.toSupportedUrl()
                return LoadData(url, SVGADrawableDataFetcher(url, context))
            }

            override fun handles(model: String): Boolean {
                return model.toUri().path?.endsWith(".svga", ignoreCase = true) ?: false
            }
        }
    }

    override fun teardown() {
        // Do nothing.
    }

}

/** GlideUrl 链接*/
class GlideUrl2SVGADrawableLoaderFactory(private val context: Context) : ModelLoaderFactory<GlideUrl, SVGADrawable> {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<GlideUrl?, SVGADrawable?> {
        return object : ModelLoader<GlideUrl?, SVGADrawable?> {
            override fun buildLoadData(model: GlideUrl, width: Int, height: Int, options: Options): LoadData<SVGADrawable?>? {
                return LoadData(ObjectKey(model), SVGADrawableDataFetcher(model, context))
            }

            override fun handles(model: GlideUrl): Boolean {
                return model.toURL().path.endsWith(".svga", ignoreCase = true)
            }
        }
    }

    override fun teardown() {
        // Do nothing.
    }

}