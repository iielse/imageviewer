package com.github.iielse.imageviewer

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import java.lang.IllegalArgumentException

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
            viewModel.initialize(dataProvider, transform, initialPosition)
            create().show(it.supportFragmentManager)
        }
    }

    private fun assert() {
        if (dataProvider == null) throw IllegalArgumentException("data provider is null")
        if (initialPosition >= dataProvider!!.getInitial().size) throw IllegalArgumentException("initial position should < initial photo list size")
    }
}