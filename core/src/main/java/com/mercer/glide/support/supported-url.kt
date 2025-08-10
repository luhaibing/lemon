package com.mercer.glide.support

import androidx.core.net.toUri
import com.bumptech.glide.load.model.GlideUrl

/**
 * @author      Mercer
 * @Created     2025/08/10.
 * @Description:
 *   GlideUrl 扩展
 */
data class SupportedUrl(
    val source: String,
    val key: String? = null
) : GlideUrl(source) {

    val unique: String by lazy {
        key?.ifBlank { null } ?: run {
            source.toUri().buildUpon().clearQuery().toString()
        }
    }

    override fun getCacheKey(): String? {
        return unique
    }

    override fun hashCode(): Int {
        return this.unique.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is GlideUrl -> unique == other.cacheKey
            else -> {
                source == other.toString()
            }
        }
    }

}

fun String.toSupportedUrl(): SupportedUrl {
    return SupportedUrl(this, null)
}