package com.mercer.glide.support.app

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras

/**
 * @author      Mercer
 * @Created     2025/08/24.
 * @Description:
 *   往 extras 中附加 intent.extra
 */
class IntentFallbackViewModelFactory(
    val delegate: ViewModelProvider.Factory,
    val intentBundle: Bundle? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val mutableCreationExtras = if (intentBundle == null) {
            extras
        } else {
            val defaultArgs = extras[DEFAULT_ARGS_KEY]
            val newDefaultArgs = bundleOf().apply {
                putAll(intentBundle)
                putAll(defaultArgs ?: Bundle.EMPTY)
            }
            MutableCreationExtras(extras).apply {
                set(DEFAULT_ARGS_KEY, newDefaultArgs)
            }
        }
        return delegate.create(modelClass, mutableCreationExtras)
    }

}