package com.github.iielse.imageviewer

import androidx.lifecycle.ViewModel
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.`interface`.ImageLoader
import com.github.iielse.imageviewer.datasource.Components
import com.github.iielse.imageviewer.datasource.Repository

class ImageViewerViewModel() : ViewModel() {
    private val dataProvider by lazy { Components.requireDataProvider() }
    private val repository by lazy { Repository(dataProvider) }
    val dataList = repository.itemDataSourceFactory().toLiveData(
            pageSize = Config.PAGE_SIZE
//            boundaryCallback = object : PagedList.BoundaryCallback<Item>() {
//                override fun onZeroItemsLoaded() {
//                    val list = dataProvider?.loadInitial() ?: listOf()
//                    repository.addAfter(list, initialize = true)
//                }
//
//                override fun onItemAtEndLoaded(itemAtEnd: Item) {
//                    dataProvider?.loadAfter(itemAtEnd.id) {
//                        repository.addAfter(it)
//                    }
//                }
//
//                override fun onItemAtFrontLoaded(itemAtFront: Item) {
//                    dataProvider?.loadBefore(itemAtFront.id) {
//                        repository.addBefore(it)
//                    }
//                }
//            }
    )


    fun release(){
        repository.clear()
    }
}