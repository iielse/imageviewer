package com.github.iielse.imageviewer.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.github.iielse.imageviewer.DataProvider
import com.github.iielse.imageviewer.Photo
import com.github.iielse.imageviewer.adapter.Item
import com.github.iielse.imageviewer.adapter.ItemType.PHOTO
import com.github.iielse.imageviewer.log
import kotlin.math.max
import kotlin.math.min

class Repository(private val p : DataProvider) {
    private var data = listOf<Item>()
    private var dataSource: DataSource<Int, Item>? = null

    fun addAfter(sourceList: List<Photo>, initialize: Boolean = false) {
        val result = ArrayList(data)
        if (initialize) result.clear()
        result.addAll(sourceList.map { Item(PHOTO, it.id, it) })
        data = result
        dataSource?.invalidate()
    }

    fun addBefore(sourceList: List<Photo>) {
        val result = ArrayList(data)
        result.addAll(0, sourceList.map { Item(PHOTO, it.id, it) })
        data = result
        dataSource?.invalidate()
    }

    fun clear() {
        data = listOf()
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
        private fun totalCount(): Int = data.size
        private fun loadRange(start: Int, count: Int): List<Item> {
            val dataList = data
            val totalSize = dataList.size
            val fromIndex = max(start, 0)
            val toIndex = min(start + count, totalSize)
            if (fromIndex > toIndex) return emptyList()
            return dataList.subList(fromIndex, toIndex)
        }

        private fun nodeIndex(key: Int): Int {
            return data.indexOfFirst { it.id == key }
        }

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Item>) {
//            val totalSize = totalCount()
//            val start = min(0, totalSize)
//            val loadSize = totalSize - start
//            log { "loadInitial start $start loadSize $loadSize totalSize $totalSize" }
//            callback.onResult(loadRange(start, loadSize), start, loadSize)

            val result = p.loadInitial()
            callback.onResult(result.map { Item(PHOTO, it.id, it) }, 0, result.size)

        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Item>) {
//            val nodeIdx = nodeIndex(params.key)
//            if (nodeIdx < 0) {
//                log { "loadAfter key ${params.key} nodeIdx $nodeIdx return emptyList" }
//                callback.onResult(emptyList<Item>())
//                return
//            }
//            val totalSize = totalCount()
//            val start = min(nodeIdx + 1, totalSize)
//            val loadSize = min(params.requestedLoadSize, totalSize - start)
//            log { "loadAfter key ${params.key} nodeIdx $nodeIdx start $start loadSize $loadSize totalSize $totalSize" }
//            callback.onResult(loadRange(start, loadSize))
            p.loadAfter(params.key) {
                callback.onResult(it.map { Item(PHOTO, it.id, it) })
            }
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Item>) {
//            val nodeIdx = nodeIndex(params.key)
//            if (nodeIdx <= 0) {
//                log { "loadBefore key ${params.key} nodeIdx $nodeIdx return emptyList" }
//                callback.onResult(emptyList<Item>())
//                return
//            }
//            val totalSize = totalCount()
//            val end = min(nodeIdx - 1, totalSize)
//            val start = max(end - params.requestedLoadSize, 0)
//            val loadSize = min(params.requestedLoadSize, end - start)
//            log { "loadBefore key ${params.key} nodeIdx $nodeIdx start $start end $end loadSize $loadSize totalSize $totalSize" }
//            callback.onResult(loadRange(start, loadSize))

            p.loadBefore(params.key) {
                callback.onResult(it.map { Item(PHOTO, it.id, it) })
            }
        }

        override fun getKey(item: Item): Int {
            return item.id
        }
    }
}

