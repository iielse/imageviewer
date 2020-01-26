package com.github.iielse.imageviewer

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.github.iielse.imageviewer.core.ImageLoader
import com.github.iielse.imageviewer.core.Components
import com.github.iielse.imageviewer.core.DataProvider
import com.github.iielse.imageviewer.core.DefaultTransformer
import com.github.iielse.imageviewer.core.Transformer

class ImageViewerBuilder(private val context: Context?,
                         private val imageLoader: ImageLoader,
                         private val dataProvider: DataProvider,
                         private val transformer: Transformer = DefaultTransformer(),
                         private val initKey: Int = 0
) {
    private fun create(): ImageViewerDialogFragment {
        return ImageViewerDialogFragment()
    }

    fun show() {
        (context as? FragmentActivity?)?.let {
            Components.set(imageLoader, dataProvider, transformer, initKey)
            val viewer = create()
            Components.attach(viewer)
            viewer.show(it.supportFragmentManager)
        }
    }

}