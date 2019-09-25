package com.github.iielse.imageviewer.core

import androidx.paging.PageKeyedDataSource
import kotlin.math.min

abstract class StorePageKeyedDataSource : PageKeyedDataSource<Int, PWrapper>() {
    abstract fun totalCount(): Int
    abstract fun loadRange(start: Int, count: Int): List<PWrapper?>

    override fun loadInitial(
            params: LoadInitialParams<Int>,
            callback: LoadInitialCallback<Int, PWrapper>
    ) {
        val totalSize = totalCount()
        if (totalSize == 0) {
            callback.onResult(emptyList<PWrapper>(), 0, 0, null, 0)
            return
        }

        val result = loadRange(0, totalSize)
        if (result.size == totalSize) {
            callback.onResult(result, 0, totalSize, null, result.size)
        } else {
            // null list, or size doesn't match request - DataStore modified between count and load
            invalidate()
        }
    }

    override fun loadAfter(
            params: LoadParams<Int>,
            callback: LoadCallback<Int, PWrapper>
    ) {
        val totalCount = totalCount()
        val startPosition = min(totalCount, params.key)
        val loadSize = computeLoadSize(totalCount, startPosition, params.requestedLoadSize)
        val result = loadRange(startPosition, loadSize)
        callback.onResult(result, startPosition + result.size)
    }

    private fun computeLoadSize(
            totalSize: Int,
            loadPosition: Int,
            requestedLoadSize: Int
    ): Int {
        return if (loadPosition > totalSize) {
            0
        } else {
            min(totalSize - loadPosition, requestedLoadSize)
        }
    }

    override fun loadBefore(
            params: LoadParams<Int>,
            callback: LoadCallback<Int, PWrapper>
    ) {
        // ignore
    }

}