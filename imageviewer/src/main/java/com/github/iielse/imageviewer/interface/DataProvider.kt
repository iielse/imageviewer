package com.github.iielse.imageviewer.`interface`

import android.util.SparseArray
import android.widget.ImageView
import com.github.iielse.imageviewer.model.Photo

interface DataProvider {
    fun getInitial(): SparseArray<Pair<ImageView, Photo>>
    fun getMore(callback: (List<Photo>) -> Unit)
}

abstract class DataProviderAdapter : DataProvider {
    abstract override fun getInitial(): SparseArray<Pair<ImageView, Photo>>

    override fun getMore(callback: (List<Photo>) -> Unit) {
        callback(emptyList())
    }
}