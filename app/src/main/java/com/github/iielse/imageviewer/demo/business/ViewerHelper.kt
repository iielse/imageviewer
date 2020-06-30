package com.github.iielse.imageviewer.demo.business

import androidx.fragment.app.FragmentActivity
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.ImageViewerDialogFragment
import com.github.iielse.imageviewer.core.DataProvider
import com.github.iielse.imageviewer.demo.core.viewer.*
import com.github.iielse.imageviewer.demo.data.Api
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.data.myData

/**
 * viewer的自定义初始化方案
 */
object ViewerHelper {
    var loadAllAtOnce: Boolean = false
    var fullScreen: Boolean = false
    var simplePlayVideo: Boolean = true

    fun provideImageViewerBuilder(context: FragmentActivity, clickedData: MyData): ImageViewerBuilder {
        val builder = ImageViewerBuilder(
                context = context,
                initKey = clickedData.id,
                dataProvider = myDataProvider(clickedData),
                imageLoader = MyImageLoader(),
                transformer = MyTransformer()
        )

        MyViewerEx(context).attach(builder)

        if (fullScreen) {
            builder.setViewerFactory(object : ImageViewerDialogFragment.Factory() {
                override fun build() = FullScreenImageViewerDialogFragment()
            })
        }
        return builder
    }

    private fun myDataProvider(clickedData: MyData): DataProvider {
        return if (loadAllAtOnce) {
            provideViewerDataProvider { myData }
        } else {
            provideViewerDataProvider(
                    loadInitial = { listOf(clickedData) },
                    loadAfter = { id, callback -> Api.asyncQueryAfter(id, callback) },
                    loadBefore = { id, callback -> Api.asyncQueryBefore(id, callback) }
            )
        }
    }
}

