package com.github.iielse.imageviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.iielse.imageviewer.core.Photo

class ImageViewerActionViewModel : ViewModel() {
    private val _actionEvent = MutableLiveData<Pair<String, Any?>>()
    val actionEvent: LiveData<Pair<String, Any?>> = _actionEvent

    fun setCurrentItem(pos: Int) = internalHandle(ViewerActions.SET_CURRENT_ITEM, pos)
    fun dismiss() = internalHandle(ViewerActions.DISMISS, null)
    fun remove(item: List<Photo>) = internalHandle(ViewerActions.REMOVE_ITEMS, item)

    private fun internalHandle(action: String, extra: Any?) {
        _actionEvent.value = Pair(action, extra)
        _actionEvent.value = null
    }
}