package com.github.iielse.imageviewer.demo.core.viewer

import com.github.iielse.imageviewer.core.DataProvider
import com.github.iielse.imageviewer.core.Photo

// 自定义加载数据方案
fun provideViewerDataProvider(
        loadAfter: ((Long, (List<Photo>) -> Unit) -> Unit)? = null,
        loadBefore: ((Long, (List<Photo>) -> Unit) -> Unit)? = null,
        loadInitial: (() -> List<Photo>)
): DataProvider {
    return object : DataProvider {
        override fun loadInitial(): List<Photo> = loadInitial()
        override fun loadAfter(key: Long, callback: (List<Photo>) -> Unit) {
            loadAfter?.invoke(key, callback)
        }

        override fun loadBefore(key: Long, callback: (List<Photo>) -> Unit) {
            loadBefore?.invoke(key, callback)
        }
    }
}