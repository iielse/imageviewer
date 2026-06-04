package com.github.iielse.imageviewer.demo.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.liveData
import com.github.iielse.imageviewer.demo.business.ItemType
import com.github.iielse.imageviewer.demo.core.BaseItemType
import com.github.iielse.imageviewer.demo.core.Cell
import com.github.iielse.imageviewer.demo.core.ID_EMPTY
import com.github.iielse.imageviewer.demo.data.Service.api
import com.github.iielse.imageviewer.demo.utils.PAGE_SIZE

// 主页面的数据
class TestRepository {
    private var pagingSource: TestPagingSource? = null

    val dataList: LiveData<PagingData<Cell>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            enablePlaceholders = false,
        )
    ) {
        TestPagingSource(api).also { pagingSource = it }
    }.liveData

    fun request() = Unit

    // 清除本地数据
    fun localDelete(item: List<MyData>) {
        if (item.isEmpty()) return
        pagingSource?.invalidate()
    }

    companion object {
        private val inst by lazy { TestRepository() }
        fun get() = inst
    }
}

private class TestPagingSource(
    private val api: Api,
) : PagingSource<Long, Cell>() {

    override fun getRefreshKey(state: PagingState<Long, Cell>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Cell> {
        return try {
            val key = params.key ?: -1L
            val data = api.queryAfter(key, params.loadSize)
            val items = if (key == -1L && data.isEmpty()) {
                listOf(Cell(BaseItemType.Empty, ID_EMPTY))
            } else {
                data.map {
                    Cell(ItemType.TestData, it.id.toString(), it)
                }
            }
            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = data.lastOrNull()?.id?.takeIf { data.size >= params.loadSize },
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
