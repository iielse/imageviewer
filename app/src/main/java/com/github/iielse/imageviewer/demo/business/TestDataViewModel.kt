package com.github.iielse.imageviewer.demo.business

import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.demo.data.*
import com.github.iielse.imageviewer.demo.utils.log
import kotlin.math.min

class TestDataViewModel : ViewModel() {
    private val dataApi = api
    private val data: MutableList<MyData> = myData

    private var dataSource: DataSource<Long, MyData>? = null
    // demo 测试数据源
    val dataList = testDataSourceFactory().toLiveData(pageSize = 1)

    fun reduceRemoveAll(item: List<MyData>)  {
        data.removeAll(item)
        log("reduceRemoveAll")
        dataSource?.invalidate()
    }

    private fun testDataSourceFactory(): DataSource.Factory<Long, MyData> {
        return object : DataSource.Factory<Long, MyData>() {
            override fun create(): DataSource<Long, MyData> {
                return object : ItemKeyedDataSource<Long, MyData>() {
                    override fun getKey(item: MyData): Long = item.id
                    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<MyData>) {
                        val result = data.toList().subList(0, min(data.size, PAGE_SIZE))
                        callback.onResult(result, 0, result.size)
                    }

                    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<MyData>) {
                        dataApi.asyncQueryAfter(params.key) {
                            callback.onResult(it)
                        }
                    }

                    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<MyData>) {
                        dataApi.asyncQueryBefore(params.key) {
                            callback.onResult(it)
                        }
                    }
                }.also { dataSource = it }
            }
        }
    }
}