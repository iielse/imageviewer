package com.github.iielse.imageviewer

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.github.iielse.imageviewer.`interface`.DataProvider
import com.github.iielse.imageviewer.`interface`.Transform

class ImageViewerBuilder(private val context: Context?) {
    private var dataProvider: DataProvider? = null
    private var transform: Transform? = null

    fun setDataProvider(provider: DataProvider?): ImageViewerBuilder {
        return this.apply { dataProvider = provider }
    }

    fun setTransform(trans: Transform?): ImageViewerBuilder {
        return this.apply { transform = trans }
    }

    private fun create(): ImageViewerDialogFragment {
        return ImageViewerDialogFragment()
    }

    fun show() {
        (context as? FragmentActivity?)?.let {
            val viewModel = ViewModelProviders.of(it).get(ImageViewerViewModel::class.java)
            viewModel.initialize(dataProvider, transform)
            create().show(it.supportFragmentManager)
        }
    }
}