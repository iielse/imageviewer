package com.github.iielse.imageviewer.adapter

import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.github.iielse.imageviewer.core.Components
import com.github.iielse.imageviewer.core.Photo
import kotlinx.coroutines.suspendCancellableCoroutine

class Repository {
    private val dataProvider by lazy { Components.requireDataProvider() }
    private val dataList = MutableLiveData<List<Photo>>()
    internal val snapshot: List<Photo> get() = dataList.value ?: listOf()
    internal val pagingData = Pager(PagingConfig(1),null) { dataSource() }.liveData

    private fun dataSource() = object : PagingSource<Long, Photo>() {
        override fun getRefreshKey(state: PagingState<Long, Photo>): Long? = null
        override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Photo> {
            when (params) {
                is LoadParams.Refresh -> {
                    val list: List<Photo> = snapshot.ifEmpty { dataProvider.loadInitial() }
                    dataList.value = list
                    return LoadResult.Page(list, list.firstOrNull()?.id(), list.lastOrNull()?.id())
                }
                is LoadParams.Append -> {
                    val list: List<Photo> = suspendCancellableCoroutine { continuation ->
                        dataProvider.loadAfter(params.key) {
                            continuation.resume(it, null)
                        }
                    }
                    dataList.value = snapshot.toMutableList().also { it.addAll(list) }
                    return LoadResult.Page(list, list.firstOrNull()?.id(), list.lastOrNull()?.id())
                }
                is LoadParams.Prepend -> {
                    val list: List<Photo> = suspendCancellableCoroutine { continuation ->
                        dataProvider.loadBefore(params.key) {
                            continuation.resume(it, null)
                        }
                    }
                    dataList.value = snapshot.toMutableList().also { it.addAll(0, list) }
                    return LoadResult.Page(list, list.firstOrNull()?.id(), list.lastOrNull()?.id())
                }
            }
        }
    }

    fun redirect(adapter: ImageViewerAdapter, exclude: List<Photo>, emptyCallback: () -> Unit) {
        val last = exclude.maxOf { it.id() }
        val list = snapshot
        val target = list.findLast { it.id() < last }
            ?: list.find { it.id() > last }
            ?: return Unit.also { emptyCallback() }
        dataList.value = listOf(target)
        adapter.refresh()
    }
}
