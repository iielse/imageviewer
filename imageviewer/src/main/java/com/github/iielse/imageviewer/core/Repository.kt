package com.github.iielse.imageviewer.core

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.github.iielse.imageviewer.adapter.Item
import com.github.iielse.imageviewer.adapter.ItemType.PHOTO
import com.github.iielse.imageviewer.utils.log

class Repository {
    private val dataProvider by lazy { Components.requireDataProvider() }

    fun dataSourceFactory(): DataSource.Factory<Int, Item> {
        return object : DataSource.Factory<Int, Item>() {
            override fun create(): DataSource<Int, Item> {
                return dataSource()
            }
        }
    }

    private fun dataSource() = object : ItemKeyedDataSource<Int, Item>() {
        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Item>) {
            val result = dataProvider.loadInitial()
            log { "loadInitial ${result.size}" }
            callback.onResult(result.map { Item(PHOTO, it.id(), it) }, 0, result.size)
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Item>) {
            dataProvider.loadAfter(params.key) {
                log { "loadAfter ${params.key} ${it.size}" }
                callback.onResult(it.map { Item(PHOTO, it.id(), it) })
            }
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Item>) {
            dataProvider.loadBefore(params.key) {
                log { "loadBefore ${params.key} ${it.size}" }
                callback.onResult(it.map { Item(PHOTO, it.id(), it) })
            }
        }

        override fun getKey(item: Item): Int {
            return item.id
        }
    }
}

