package com.github.iielse.imageviewer.demo.data

import androidx.paging.DataSource
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.demo.business.ItemType
import com.github.iielse.imageviewer.demo.core.*
import com.github.iielse.imageviewer.demo.utils.PAGE_SIZE
import com.github.iielse.imageviewer.demo.utils.runOnWorkThread


// 主页面的数据
class TestRepository {
    private var dataSource: XPageKeyedDataSource? = null
    private val dataSourceFactory = object : DataSource.Factory<Int, Cell>() {
        override fun create() = object : XPageKeyedDataSource() {
            override fun totalCount() = state.list.size
            override fun loadRange(start: Int, count: Int): List<Cell> {
                return state.list.mapToCell(start, count) {
                    Cell(ItemType.TestData, it, state.data[it])
                }
            }
        }.also { dataSource = it }
    }
    private var state = ListState<Photo>()
    val dataList = dataSourceFactory.toLiveData(
        cellId = { it.id },
        requestMore = { request(false) }
    )

    // 分页加载
    fun request(initial: Boolean) {
        val requestKey = if (initial) -1 else state.nextKey?.toLong()
        Service.api.asyncQueryAfter(requestKey, PAGE_SIZE) {
            runOnWorkThread {
                state = state.reduceOnNext(initial, it) { it.id().toString() }
                dataSource?.invalidate()
            }
        }
    }

    // 清除本地数据
    fun localDelete(item: List<MyData>) = runOnWorkThread {
        state = state.copy(
            list = state.list.removeAll(item.map { it.id.toString() })
        )
        dataSource?.invalidate()
    }

    companion object {
        private val inst by lazy { TestRepository() }
        fun get() = inst
    }
}

