package com.github.iielse.imageviewer.demo.core

import androidx.paging.PageKeyedDataSource
import kotlin.math.min

abstract class XPageKeyedDataSource : PageKeyedDataSource<Int, Cell>() {
    abstract fun totalCount(): Int
    abstract fun loadRange(start: Int, count: Int): List<Cell>

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Cell>
    ) {
        val totalSize = totalCount()
        if (totalSize == 0) {
            callback.onResult(emptyList<Cell>(), 0, 0, null, 0)
            return
        }

        val initialStart = 0
        val result = loadRange(initialStart, totalSize)
        if (result.size == totalSize) {
            callback.onResult(result, initialStart, totalSize, null, result.size)
        } else {
            // null list, or size doesn't match request - DataStore modified between count and load
            invalidate()
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Cell>
    ) {
        // ignore
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, Cell>
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
    ): Int = if (loadPosition > totalSize) 0 else min(totalSize - loadPosition, requestedLoadSize)
}