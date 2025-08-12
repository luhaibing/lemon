package com.mercer.glide.support

import android.util.Log

/**
 * @author      Mercer
 * @Created     2025/08/10.
 * @Description:
 *   日志记录
 */
const val TAG = "GlideSupport"

fun e(vararg values: Any) {
    val message = values.joinToString(separator = " >>> ", transform = Any::toString)
    if (BuildConfig.DEBUG) {
        Log.e(TAG, message)
    }
}

fun d(vararg values: Any) {
    val message = values.joinToString(separator = " >>> ", transform = Any::toString)
    if (BuildConfig.DEBUG) {
        Log.d(TAG, message)
    }
}