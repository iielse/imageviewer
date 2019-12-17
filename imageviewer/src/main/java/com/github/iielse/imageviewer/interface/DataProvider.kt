package com.github.iielse.imageviewer.`interface`

import com.github.iielse.imageviewer.model.Photo

interface DataProvider {
    fun getInitial(): List<Photo>
    fun getMore(callback: (List<Photo>) -> Unit)
}

abstract class DataProviderAdapter : DataProvider {
    abstract override fun getInitial(): List<Photo>

    override fun getMore(callback: (List<Photo>) -> Unit) {
        callback(emptyList())
    }
}