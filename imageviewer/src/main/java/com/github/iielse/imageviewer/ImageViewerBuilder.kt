package com.github.iielse.imageviewer

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.github.iielse.imageviewer.`interface`.ImageLoader
import com.github.iielse.imageviewer.datasource.Components

class ImageViewerBuilder(private val context: Context?,
                         private val imageLoader: ImageLoader,
                         private val dataProvider: DataProvider,
                         private val transformer: Transformer = DefaultTransformer(),
                         private val initialPosition: Int = 0
) {
    private fun create(): ImageViewerDialogFragment {
        return ImageViewerDialogFragment()
    }

    fun show() {
        (context as? FragmentActivity?)?.let {
            Components.set(imageLoader, dataProvider, transformer, initialPosition)
            val viewer = create()
            Components.attach(viewer)
            viewer.show(it.supportFragmentManager)
        }
    }

}