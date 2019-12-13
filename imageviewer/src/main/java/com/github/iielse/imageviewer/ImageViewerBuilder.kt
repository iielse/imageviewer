package com.github.iielse.imageviewer

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.github.iielse.imageviewer.`interface`.DataProvider
import com.github.iielse.imageviewer.`interface`.DataProviderAdapter

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
            val viewModel = ViewModelProviders.of(it).get(ImageViewerViewModel::class.java)
            viewModel.initialize(dataProvider ?: DataProviderAdapter())

            create().show(it.supportFragmentManager, ImageViewerDialogFragment::class.java.simpleName)
        }
    }
}