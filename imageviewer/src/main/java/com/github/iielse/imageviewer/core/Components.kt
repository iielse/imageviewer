package com.github.iielse.imageviewer.core

import androidx.lifecycle.LifecycleOwner
import com.github.iielse.imageviewer.utils.log
import com.github.iielse.imageviewer.utils.onDestroy
import kotlin.IllegalStateException

object Components {
    private var initialize = false
    val working get() = initialize
    private var imageLoader: ImageLoader? = null
    private var dataProvider: DataProvider? = null
    private var transformer: Transformer? = null
    private var initKey: Long? = null
    private var vhCustomizer: VHCustomizer? = null
    private var overlayCustomizer: OverlayCustomizer? = null
    private var viewerCallback: ViewerCallback? = null

    fun initialize(imageLoader: ImageLoader, dataProvider: DataProvider, transformer: Transformer, initKey: Long) {
        log { "Components initialize" }
        if (initialize) throw IllegalStateException()
        Components.imageLoader = imageLoader
        Components.dataProvider = dataProvider
        Components.transformer = transformer
        Components.initKey = initKey
        initialize = true
    }

    fun setVHCustomizer(vhCustomizer: VHCustomizer?) {
        Components.vhCustomizer = vhCustomizer
    }

    fun setViewerCallback(viewerCallback: ViewerCallback?) {
        Components.viewerCallback = viewerCallback
    }

    fun setOverlayCustomizer(overlayCustomizer: OverlayCustomizer?) {
        Components.overlayCustomizer = overlayCustomizer
    }

    fun attach(owner: LifecycleOwner) {
        owner.onDestroy { release() }
    }

    fun requireImageLoader() = imageLoader!!
    fun requireDataProvider() = dataProvider!!
    fun requireTransformer() = transformer!!
    fun requireInitKey() = initKey!!
    fun requireVHCustomizer() = vhCustomizer ?: object : VHCustomizer {}
    fun requireViewerCallback() = viewerCallback ?: object : ViewerCallback {}
    fun requireOverlayCustomizer() = overlayCustomizer ?: object : OverlayCustomizer {}

    private fun release() {
        log { "Components release" }
        initialize = false
        imageLoader = null
        dataProvider = null
        transformer = null
        initKey = null
        vhCustomizer = null
        viewerCallback = null
        overlayCustomizer = null
    }
}