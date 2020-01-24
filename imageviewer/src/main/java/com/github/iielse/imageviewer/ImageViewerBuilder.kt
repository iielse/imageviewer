package com.github.iielse.imageviewer

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders

class ImageViewerBuilder(private val context: Context?) {
    private var dataProvider: DataProvider? = null
    private var transform: Transform? = null
    private var initialPosition = 0

    fun setDataProvider(provider: DataProvider?): ImageViewerBuilder {
        return this.apply { dataProvider = provider }
    }

    fun setTransform(trans: Transform?): ImageViewerBuilder {
        return this.apply { transform = trans }
    }

    fun setInitialPosition(pos : Int) : ImageViewerBuilder {
        return this.apply { initialPosition = pos }

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