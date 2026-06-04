package com.github.iielse.imageviewer

import androidx.lifecycle.ViewModel
import com.github.iielse.imageviewer.core.Photo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ImageViewerActionViewModel : ViewModel() {
    private val _actionEvent = MutableSharedFlow<Pair<String, Any?>>(extraBufferCapacity = 1)
    val actionEvent: SharedFlow<Pair<String, Any?>> = _actionEvent.asSharedFlow()

    fun setCurrentItem(pos: Int) = internalHandle(ViewerActions.SET_CURRENT_ITEM, pos)
    fun dismiss() = internalHandle(ViewerActions.DISMISS, null)
    fun remove(item: List<Photo>) = internalHandle(ViewerActions.REMOVE_ITEMS, item)

    private fun internalHandle(action: String, extra: Any?) {
        _actionEvent.tryEmit(action to extra)
    }
}
