package com.mercer.glide.support.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat

/**
 * @author      Mercer
 * @Created     2025/08/24.
 * @Description:
 *
 */
class BlankViewModel(
    application: Application,
    val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

     val tsAsFlow: Flow<String>
         get() {
             return savedStateHandle.getStateFlow("k1", System.currentTimeMillis())
                 .map {
                     val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                     sdf.format(it)
                 }
         }

}