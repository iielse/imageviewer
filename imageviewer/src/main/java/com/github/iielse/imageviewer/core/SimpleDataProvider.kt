package com.github.iielse.imageviewer.core

import android.os.Handler
import android.os.Looper

import kotlin.math.min

class SimpleDataProvider(
    val init: Photo,
    val list: List<Photo>
) : DataProvider {
    override fun loadInitial() = listOf(init)
    override fun loadAfter(key: Long, callback: (List<Photo>) -> Unit) {
        val idx = list.indexOfFirst { it.id() == key }
        val result: List<Photo> = if (idx < 0) emptyList()
        else list.subList(idx + 1, list.size)
        Handler(Looper.getMainLooper()).post {
            callback(result)
        }
    }

    override fun loadBefore(key: Long, callback: (List<Photo>) -> Unit) {
        val idx = list.indexOfFirst { it.id() == key }
        val result: List<Photo> = if (idx < 0) emptyList()
        else list.subList(0, min(idx, list.size))
        Handler(Looper.getMainLooper()).post {
            callback(result)
        }
    }
}