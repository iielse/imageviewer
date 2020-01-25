package com.github.iielse.imageviewer.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.github.iielse.imageviewer.Photo
import com.github.iielse.imageviewer.adapter.Item
import com.github.iielse.imageviewer.adapter.ItemType.PHOTO
import com.github.iielse.imageviewer.log
import kotlin.math.max
import kotlin.math.min

class Repository {
    private val data = mutableListOf<Item>()
    private var dataSource: DataSource<Int, Item>? = null

    fun addAfter(sourceList: List<Photo>, initialize :Boolean = false) {
        if (initialize)  data.clear()
        data.addAll(sourceList.map { Item(PHOTO, it.id, it) })
        dataSource?.invalidate()
    }

    fun addBefore(sourceList: List<Photo>) {
        data.addAll(0, sourceList.map { Item(PHOTO, it.id, it) })
        dataSource?.invalidate()
    }
    fun itemDataSourceFactory(): DataSource.Factory<Int, Item> {
        return object : DataSource.Factory<Int, Item>() {
            override fun create(): DataSource<Int, Item> {
                return itemDataSource().also { dataSource = it }
            }
        }
    }

    private fun itemDataSource() = object : ItemKeyedDataSource<Int, Item>() {
        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Item>) {
            val start = min(0, data.size)
            val loadSize = min(params.requestedLoadSize, data.size - start)
            log { "loadInitial start $start loadSize $loadSize" }
            callback.onResult(data.subList(start, start + loadSize))
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Item>) {
            val idx = data.indexOfFirst { it.id == params.key }
            if (idx < 0) {
                callback.onResult(emptyList<Item>())
                return
            }
            val start = min(idx, data.size)
            val loadSize = min(params.requestedLoadSize, data.size - start)
            log { "loadAfter key ${params.key} idx $idx start $start loadSize $loadSize" }
            callback.onResult(data.subList(start, start + loadSize))
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Item>) {
            val idx = data.indexOfFirst { it.id == params.key }
            if (idx < 0) {
                callback.onResult(emptyList<Item>())
                return
            }
            val end = min(idx, data.size)
            val start = max(end - params.requestedLoadSize, 0)
            val loadSize = min(params.requestedLoadSize, end - start)
            log { "loadBefore key ${params.key} idx $idx start $start end $end loadSize $loadSize" }
            callback.onResult(data.subList(start, start + loadSize))
        }

        override fun getKey(item: Item): Int {
            return item.id
        }
    }
}

