package com.github.iielse.imageviewer

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.github.iielse.imageviewer.core.*

class ImageViewerBuilder(private val context: Context?,
                         private val imageLoader: ImageLoader,
                         private val dataProvider: DataProvider,
                         private val transformer: Transformer = DefaultTransformer(),
                         private val initKey: Long = 0
) {
    private var vhCustomizer: VHCustomizer? = null
    private var viewerCallback: ViewerCallbackAdapter? = null

    fun setVHCustomizer(vhCustomizer: VHCustomizer): ImageViewerBuilder {
        this.vhCustomizer = vhCustomizer
        return this
    }

    fun setViewerCallback(viewerCallback: ViewerCallbackAdapter): ImageViewerBuilder {
        this.viewerCallback = viewerCallback
        return this
    }

    private fun create(): ImageViewerDialogFragment {
        return ImageViewerDialogFragment()
    }

    fun show() {
        (context as? FragmentActivity?)?.let {
            Components.initialize(imageLoader, dataProvider, transformer, initKey)
            Components.setVHCustomizer(vhCustomizer)
            Components.setViewerCallback(viewerCallback)
            val viewer = create()
            Components.attach(viewer)
            viewer.show(it.supportFragmentManager)
        }
    }

}