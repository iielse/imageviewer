package com.github.iielse.imageviewer.adapter

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.github.iielse.imageviewer.core.Components
import com.github.iielse.imageviewer.core.Photo

class Repository {
    private val dataProvider by lazy { Components.requireDataProvider() }
    private var dataSource: DataSource<Long, Item>? = null
    private var snapshot: List<Photo>? = null

    fun dataSourceFactory(): DataSource.Factory<Long, Item> {
        return object : DataSource.Factory<Long, Item>() {
            override fun create(): DataSource<Long, Item> {
                return dataSource().also { dataSource = it }
            }
        }
    }

    private fun dataSource() = object : ItemKeyedDataSource<Long, Item>() {
        override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Item>) {
            val result = snapshot ?: dataProvider.loadInitial()
            snapshot = result
            callback.onResult(result.map { Item.from(it) }, 0, result.size)
        }

        override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Item>) {
            dataProvider.loadAfter(params.key) {
                snapshot = snapshot?.toMutableList()?.apply { addAll(it) }
                callback.onResult(it.map { photo -> Item.from(photo) })
            }
        }

        override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Item>) {
            dataProvider.loadBefore(params.key) {
                snapshot = snapshot?.toMutableList()?.apply { addAll(0, it) }
                callback.onResult(it.map { photo -> Item.from(photo) })
            }
        }

        override fun getKey(item: Item): Long {
            return item.id
        }
    }

    fun removeAll(item: List<Photo>) {
        val iterator = snapshot?.toMutableList()?.listIterator()
        while (iterator?.hasNext() == true) {
            val i = iterator.next()
            if (item.any { it.id() == i.id() }) {
                iterator.remove()
            }
        }
        snapshot = snapshot?.toMutableList()?.apply { removeAll(item) }
        dataSource?.invalidate()
    }
}
