package com.mercer.glide.support

import android.content.res.AssetManager
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

/**
 * @author      Mercer
 * @Created     2025/08/24.
 * @Description:
 *   支持读取 AssetInputStream 类型的输入
 */
class AssetInputStreamDataFetcher(
    val input:AssetManager.AssetInputStream
) : DataFetcher<InputStream>{

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        callback.onDataReady(input)
    }

    override fun cleanup() {
    }

    override fun cancel() {
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }

}
class AssetInputStreamLoaderFactory : ModelLoaderFactory<AssetManager.AssetInputStream,InputStream > {

    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<AssetManager.AssetInputStream, InputStream> {
        return object : ModelLoader<AssetManager.AssetInputStream, InputStream> {

            override fun buildLoadData(model: AssetManager.AssetInputStream, width: Int, height: Int, options: Options): LoadData<InputStream?>? {
                return LoadData(ObjectKey(model), AssetInputStreamDataFetcher(model))
            }

            override fun handles(value: AssetManager.AssetInputStream): Boolean {
                return true
            }
        }
    }

    override fun teardown() {
    }

}