package com.github.iielse.imageviewer.datasource

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.github.iielse.imageviewer.DataProvider
import com.github.iielse.imageviewer.Transformer
import com.github.iielse.imageviewer.`interface`.ImageLoader
import com.github.iielse.imageviewer.log

object Components {
    private var working = false
    private var imageLoader: ImageLoader? = null
    private var dataProvider: DataProvider? = null
    private var transformer: Transformer? = null
    private var initKey: Int? = null

    fun set(imageLoader: ImageLoader, dataProvider: DataProvider, transformer: Transformer, initKey: Int) {
        log { "Components set" }
        this.imageLoader = imageLoader
        this.dataProvider = dataProvider
        this.transformer = transformer
        this.initKey = initKey
    }

    fun attach(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() = release()
        })
    }

    fun requireImageLoader() = imageLoader!!
    fun requireDataProvider() = dataProvider!!
    fun requireTransformer() = transformer!!
    fun requireInitKey() = initKey!!

    private fun release() {
        log { "Components release" }
        working = false
        imageLoader = null
        dataProvider = null
        transformer = null
        initKey = null
    }
}