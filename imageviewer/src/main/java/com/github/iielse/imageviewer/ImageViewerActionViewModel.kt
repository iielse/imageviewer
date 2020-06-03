package com.github.iielse.imageviewer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.iielse.imageviewer.core.Photo

object ViewerActions {
    const val SET_CURRENT_ITEM = "setCurrentItem"
    const val DISMISS = "dismiss"
    const val REMOVE_ITEMS = "removeItems"
}

class ImageViewerActionViewModel : ViewModel() {

    val actionEvent = MutableLiveData<Pair<String, Any?>>()

    fun setCurrentItem(pos: Int) = internalHandle(ViewerActions.SET_CURRENT_ITEM, pos)
    fun dismiss() = internalHandle(ViewerActions.DISMISS, null)
    fun remove(item: List<Photo>) = internalHandle(ViewerActions.REMOVE_ITEMS, item)

    private fun internalHandle(action: String, extra: Any?) {
        actionEvent.value = Pair(action, extra)
        actionEvent.value = null
    }
}