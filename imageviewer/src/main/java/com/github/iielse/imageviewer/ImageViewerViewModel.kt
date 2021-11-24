package com.github.iielse.imageviewer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.adapter.Repository
import com.github.iielse.imageviewer.core.Photo

@Suppress("UNCHECKED_CAST")
class ImageViewerViewModel : ViewModel() {
    private val repository = Repository()
    val dataList = repository.createDataSource().toLiveData(pageSize = 1)
    val viewerUserInputEnabled = MutableLiveData<Boolean>()

    fun setViewerUserInputEnabled(enable: Boolean) {
        if (viewerUserInputEnabled.value != enable) viewerUserInputEnabled.value = enable
    }

    fun remove(item: Any?, emptyCallback: () -> Unit) {
        val removed = (item as? List<Photo>?) ?: return
        repository.redirect(removed, emptyCallback)
    }
}