package com.github.iielse.imageviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.github.iielse.imageviewer.adapter.ImageViewerAdapter
import com.github.iielse.imageviewer.adapter.Repository
import com.github.iielse.imageviewer.core.Photo

@Suppress("UNCHECKED_CAST")
class ImageViewerViewModel : ViewModel() {
    private val repository = Repository()
    val snapshot: List<Photo> get() = repository.snapshot
    val initialIndex: LiveData<Int?> = repository.initialIndex
    val pagingData: LiveData<PagingData<Photo>> = repository.pagingData
    val viewerUserInputEnabled = MutableLiveData<Boolean>()

    fun setViewerUserInputEnabled(enable: Boolean) {
        if (viewerUserInputEnabled.value != enable) viewerUserInputEnabled.value = enable
    }

    fun remove(adapter: ImageViewerAdapter, item: Any?, emptyCallback: () -> Unit) {
        val removed = (item as? List<Photo>?) ?: return
        repository.redirect(adapter, removed, emptyCallback)

    }
}