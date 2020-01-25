package com.github.iielse.imageviewer

import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.adapter.Item
import com.github.iielse.imageviewer.datasource.Repository

class ImageViewerViewModel : ViewModel() {
    var dataProvider: DataProvider? = null
    var transform: Transform? = null
    var initialPosition: Int = 0

    fun initialize(dataProvider: DataProvider?, transform: Transform?, initialPos: Int?) {
        this.dataProvider = dataProvider
        this.transform = transform
        this.initialPosition = initialPos ?: 0
    }

    val repository = Repository()

    val dataList = repository.itemDataSourceFactory().toLiveData(
            pageSize = Config.PAGE_SIZE,
            boundaryCallback = object : PagedList.BoundaryCallback<Item>() {
                override fun onZeroItemsLoaded() {
                    val list = dataProvider?.loadInitial() ?: listOf()
                    repository.addAfter(list, initialize = true)
                }

                override fun onItemAtEndLoaded(itemAtEnd: Item) {
                    dataProvider?.loadAfter(itemAtEnd.id) {
                        repository.addAfter(it)
                    }
                }

                override fun onItemAtFrontLoaded(itemAtFront: Item) {
                    dataProvider?.loadBefore(itemAtFront.id) {
                        repository.addBefore(it)
                    }
                }
            }
    )
}