package com.github.iielse.imageviewer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.adapter.Repository
import com.github.iielse.imageviewer.core.Photo

class ImageViewerViewModel : ViewModel() {
    private val repository = Repository()
    val dataList = repository.dataSourceFactory().toLiveData(PagedList.Config.Builder().setPageSize(1).build())
    val viewerUserInputEnabled = MutableLiveData<Boolean>()

    fun setViewerUserInputEnabled(enable: Boolean) {
        if (viewerUserInputEnabled.value != enable) viewerUserInputEnabled.value = enable
    }

    @Suppress("UNCHECKED_CAST")
    fun remove(item: Any?) {
        (item as? List<Photo>?)?.let { repository.removeAll(it) }
    }

    val actionEvent = MutableLiveData<Pair<String, Any?>>()

    fun setCurrentItem(pos: Int) = internalHandle(ViewerActions.SET_CURRENT_ITEM, pos)
    fun dismiss() = internalHandle(ViewerActions.DISMISS, null)
    fun remove(item: List<Photo>) = internalHandle(ViewerActions.REMOVE_ITEMS, item)

    private fun internalHandle(action: String, extra: Any?) {
        actionEvent.value = Pair(action, extra)
        actionEvent.value = null
    }
}