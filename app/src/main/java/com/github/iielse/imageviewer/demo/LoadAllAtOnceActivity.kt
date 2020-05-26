package com.github.iielse.imageviewer.demo

import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.ImageViewerDialogFragment
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.viewer.*
import kotlinx.android.synthetic.main.activity_10.*

open class LoadAllAtOnceActivity : MainActivity6() {
    override fun builder(clickedData: MyData): ImageViewerBuilder {
        return ImageViewerBuilder(
                context = this,
                initKey = clickedData.id,
                dataProvider = MyDataLoadAllAtOnceProvider(),
                imageLoader = MySimpleLoader(),
                transformer = MyTransformer()
        ).also {
            myCustomController.init(it)
            if (fullScreen.tag as? Boolean? == true) {
                it.setViewerFactory(object : ImageViewerDialogFragment.Factory() {
                    override fun build(): ImageViewerDialogFragment {
                        return FullScreenImageViewerDialogFragment()
                    }
                })
            }
        }
    }
}