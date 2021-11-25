package com.github.iielse.imageviewer.adapter

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.core.Components
import com.github.iielse.imageviewer.core.Photo

class Repository {
    private val dataProvider by lazy { Components.requireDataProvider() }
    private val lock = Any()
    private var snapshot: List<Photo>? = null
    private var dataSource: DataSource<Long, Item>? = null
    private val dataSourceFactory = object : DataSource.Factory<Long, Item>() {
        override fun create() = dataSource().also { dataSource = it }
    }
    val dataList = dataSourceFactory.toLiveData(pageSize = 1)
    private fun dataSource() = object : ItemKeyedDataSource<Long, Item>() {
        override fun getKey(item: Item) = item.id
        override fun loadInitial(
            params: LoadInitialParams<Long>,
            callback: LoadInitialCallback<Item>
        ) {
            val result: List<Photo>
            synchronized(lock) {
                result = snapshot ?: dataProvider.loadInitial()
                snapshot = result
            }
            callback.onResult(result.map { Item.from(it) }, 0, result.size)
        }

        override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Item>) {
            dataProvider.loadAfter(params.key) {
                synchronized(lock) {
                    snapshot = snapshot?.toMutableList()?.apply { addAll(it) }
                }
                callback.onResult(it.map { photo -> Item.from(photo) })
            }
        }

        override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Item>) {
            dataProvider.loadBefore(params.key) {
                synchronized(lock) {
                    snapshot = snapshot?.toMutableList()?.apply { addAll(0, it) }
                }
                callback.onResult(it.map { photo -> Item.from(photo) })
            }
        }
    }

    fun redirect(exclude: List<Photo>, emptyCallback: () -> Unit) {
        val last = exclude.maxOf { it.id() }
        val list = snapshot?.toList()

        val target = list?.findLast { it.id() < last }
            ?: list?.find { it.id() > last }
            ?: return Unit.also { emptyCallback() }

        snapshot = listOf(target)
        dataSource?.invalidate()
    }
}
