package com.github.iielse.imageviewer.core

interface DataProvider {
    fun loadInitial(): List<Photo>
    fun loadAfter(key: Long, callback: (List<Photo>) -> Unit)
    fun loadBefore(key: Long, callback: (List<Photo>) -> Unit)
}

abstract class DataProviderAdapter : DataProvider {
    abstract override fun loadInitial(): List<Photo>

    override fun loadAfter(key: Long, callback: (List<Photo>) -> Unit) {
        callback(emptyList())
    }

    override fun loadBefore(key: Long, callback: (List<Photo>) -> Unit) {
        callback(emptyList())
    }
}