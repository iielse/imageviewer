package com.github.iielse.imageviewer

import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.adapter.Repository

class ImageViewerViewModel : ViewModel() {
    val dataList = Repository().dataSourceFactory().toLiveData(PagedList.Config.Builder().setPageSize(1).build())
}