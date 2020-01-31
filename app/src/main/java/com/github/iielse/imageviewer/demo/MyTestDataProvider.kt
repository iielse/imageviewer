package com.github.iielse.imageviewer.demo

import android.os.Handler
import android.os.Looper
import com.github.iielse.imageviewer.core.DataProvider
import com.github.iielse.imageviewer.core.Photo

class MyTestDataProvider(private val initPhoto: Photo) : DataProvider {
    override fun loadInitial(): List<Photo> {
        return listOf(initPhoto)
    }

    override fun loadAfter(key: Long, callback: (List<Photo>) -> Unit) {
        queryAfter(key, callback)
    }

    override fun loadBefore(key: Long, callback: (List<Photo>) -> Unit) {
        queryBefore(key, callback)
    }
}

