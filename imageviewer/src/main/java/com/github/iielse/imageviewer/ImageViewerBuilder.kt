package com.github.iielse.imageviewer

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.github.iielse.imageviewer.`interface`.DataProvider

class ImageViewerBuilder(private val context: Context?) {
    private var dataProvider: DataProvider? = null

    fun setDataProvider(dataProvider: DataProvider?): ImageViewerBuilder {
        this.dataProvider = dataProvider
        return this
    }

    fun create(): ImageViewerDialogFragment {
        return ImageViewerDialogFragment()
    }

    fun show() {
        (context as? FragmentActivity?)?.let {
            create().show(it.supportFragmentManager, ImageViewerDialogFragment::class.java.simpleName)
        }
    }
}