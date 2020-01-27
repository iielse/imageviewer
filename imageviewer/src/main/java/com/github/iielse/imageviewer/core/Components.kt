package com.github.iielse.imageviewer.core

import androidx.lifecycle.LifecycleOwner
import com.github.iielse.imageviewer.utils.log
import com.github.iielse.imageviewer.utils.onDestroy
import java.lang.IllegalStateException

object Components {
    private var working = false
    private var imageLoader: ImageLoader? = null
    private var dataProvider: DataProvider? = null
    private var transformer: Transformer? = null
    private var initKey: Long? = null

    private var vhCustomizer: VHCustomizer? = null

    fun initialize(imageLoader: ImageLoader, dataProvider: DataProvider, transformer: Transformer, initKey: Long) {
        log { "Components set" }
        if (working) throw IllegalStateException("")
        Components.imageLoader = imageLoader
        Components.dataProvider = dataProvider
        Components.transformer = transformer
        Components.initKey = initKey
        working = true
    }

    fun setVHCustomizer(vhCustomizer: VHCustomizer?) {
        Components.vhCustomizer = vhCustomizer
    }

    fun attach(owner: LifecycleOwner) {
        owner.onDestroy { release() }
    }

    fun requireImageLoader() = imageLoader!!
    fun requireDataProvider() = dataProvider!!
    fun requireTransformer() = transformer!!
    fun requireInitKey() = initKey!!
    fun requireVHCustomizer() = vhCustomizer ?: object : VHCustomizer {}

    private fun release() {
        log { "Components release" }
        working = false
        imageLoader = null
        dataProvider = null
        transformer = null
        initKey = null
    }
}