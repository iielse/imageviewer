package com.github.iielse.imageviewer.demo.business

import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import androidx.paging.toLiveData
import com.github.iielse.imageviewer.demo.data.Api
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.data.PAGE_SIZE
import com.github.iielse.imageviewer.demo.data.myData

class TestDataViewModel : ViewModel() {
    // demo 测试数据源
    val dataList = testDataSourceFactory().toLiveData(pageSize = 1)

    private fun testDataSourceFactory(): DataSource.Factory<Long, MyData> {
        return object : DataSource.Factory<Long, MyData>() {
            override fun create(): DataSource<Long, MyData> {
                return object : ItemKeyedDataSource<Long, MyData>() {
                    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<MyData>) {
                        val result = myData.subList(0, PAGE_SIZE)
                        callback.onResult(result, 0, result.size)
                    }

                    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<MyData>) {
                        Api.asyncQueryAfter(params.key) {
                            callback.onResult(it)
                        }
                    }

                    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<MyData>) {
                        Api.asyncQueryBefore(params.key) {
                            callback.onResult(it)
                        }
                    }

                    override fun getKey(item: MyData): Long {
                        return item.id
                    }
                }
            }
        }
    }
}