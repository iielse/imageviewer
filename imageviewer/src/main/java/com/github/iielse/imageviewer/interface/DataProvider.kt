package com.github.iielse.imageviewer.`interface`

import android.widget.ImageView
import com.github.iielse.imageviewer.model.Photo

interface DataProvider {
    fun getInitial(): Map<Photo, ImageView?>
    fun getMore(callback: (List<Photo>) -> Unit)
}

open class DataProviderAdapter : DataProvider {
    override fun getInitial(): Map<Photo, ImageView?> {
        return mapOf()
    }

    override fun getMore(callback: (List<Photo>) -> Unit) {
        callback(emptyList())
    }
}