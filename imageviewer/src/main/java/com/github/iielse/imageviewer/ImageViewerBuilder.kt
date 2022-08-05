package com.github.iielse.imageviewer

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.github.iielse.imageviewer.core.*

class ImageViewerBuilder(private val context: Context?,
                         private val imageLoader: ImageLoader,
                         private val dataProvider: DataProvider,
                         private val transformer: Transformer,
) {
    private var vhCustomizer: VHCustomizer? = null
    private var viewerCallback: ViewerCallback? = null
    private var overlayCustomizer: OverlayCustomizer? = null
    private var factory: ImageViewerDialogFragment.Factory? = null

    fun setVHCustomizer(vhCustomizer: VHCustomizer): ImageViewerBuilder {
        this.vhCustomizer = vhCustomizer
        return this
    }

    fun setViewerCallback(viewerCallback: ViewerCallback): ImageViewerBuilder {
        this.viewerCallback = viewerCallback
        return this
    }

    fun setOverlayCustomizer(overlayCustomizer: OverlayCustomizer?): ImageViewerBuilder {
        this.overlayCustomizer = overlayCustomizer
        return this
    }

    fun setViewerFactory(factory: ImageViewerDialogFragment.Factory?): ImageViewerBuilder {
        this.factory = factory
        return this
    }

    private fun create(): ImageViewerDialogFragment {
        return (factory ?: ImageViewerDialogFragment.Factory()).build()
    }

    fun show() {
        if (Components.working) return
        (context as? FragmentActivity?)?.let {
            Components.initialize(imageLoader, dataProvider, transformer)
            Components.setVHCustomizer(vhCustomizer)
            Components.setViewerCallback(viewerCallback)
            Components.setOverlayCustomizer(overlayCustomizer)
            val viewer = create()
            viewer.show(it.supportFragmentManager)
        }
    }

}