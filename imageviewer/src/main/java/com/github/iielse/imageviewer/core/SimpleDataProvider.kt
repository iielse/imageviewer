package com.github.iielse.imageviewer.core

import android.os.Handler
import android.os.Looper

import kotlin.math.min

class SimpleDataProvider(
    init: Photo,
    list: List<Photo>
) : DataProvider {
    private var _init = init
    private var _list = list

    override fun loadInitial() = listOf(_init)
    override fun loadAfter(key: Long, callback: (List<Photo>) -> Unit) {
        val idx = _list.indexOfFirst { it.id() == key }
        val result: List<Photo> = if (idx < 0) emptyList()
        else _list.subList(idx + 1, _list.size)
        Handler(Looper.getMainLooper()).post {
            callback(result)
        }
    }

    override fun loadBefore(key: Long, callback: (List<Photo>) -> Unit) {
        val idx = _list.indexOfFirst { it.id() == key }
        val result: List<Photo> = if (idx < 0) emptyList()
        else _list.subList(0, min(idx, _list.size))
        Handler(Looper.getMainLooper()).post {
            callback(result)
        }
    }

    override fun exclude(exclude: List<Photo>, target: Photo) {
        _init = target
        _list = _list.filter { !exclude.contains(it) }
    }
}