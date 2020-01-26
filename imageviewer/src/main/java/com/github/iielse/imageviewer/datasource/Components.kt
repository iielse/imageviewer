package com.github.iielse.imageviewer.datasource

import com.github.iielse.imageviewer.DataProvider
import com.github.iielse.imageviewer.Transformer
import com.github.iielse.imageviewer.`interface`.ImageLoader

object Components {
    private var working = false
    private var imageLoader: ImageLoader? = null
    private var dataProvider: DataProvider? = null
    private var transformer: Transformer? = null
    private var initialPosition: Int? = null

    fun set(imageLoader: ImageLoader, dataProvider: DataProvider, transformer: Transformer, initialPosition: Int) {
        this.imageLoader = imageLoader
        this.dataProvider = dataProvider
        this.transformer = transformer
        this.initialPosition = initialPosition
    }

    fun requireImageLoader() = imageLoader!!
    fun requireDataProvider() = dataProvider!!
    fun requireTransformer() = transformer!!
    fun requireInitialPosition() = initialPosition!!

    fun release() {
        working = false
        imageLoader = null
        dataProvider = null
        transformer = null
        initialPosition = null
    }
}