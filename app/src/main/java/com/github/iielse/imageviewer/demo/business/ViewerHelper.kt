package com.github.iielse.imageviewer.demo.business

import androidx.fragment.app.FragmentActivity
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.ImageViewerDialogFragment
import com.github.iielse.imageviewer.core.DataProvider
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.core.SimpleDataProvider
import com.github.iielse.imageviewer.demo.core.viewer.FullScreenImageViewerDialogFragment
import com.github.iielse.imageviewer.demo.core.viewer.MyImageLoader
import com.github.iielse.imageviewer.demo.core.viewer.MyTransformer
import com.github.iielse.imageviewer.demo.data.MyData
import com.github.iielse.imageviewer.demo.data.PAGE_SIZE
import com.github.iielse.imageviewer.demo.data.TestRepository

/**
 * viewer的自定义初始化方案
 */
object ViewerHelper {
    var orientationH: Boolean = true
    var loadAllAtOnce: Boolean = false
    var fullScreen: Boolean = false
    var simplePlayVideo: Boolean = true

    fun provideImageViewerBuilder(context: FragmentActivity, clickedData: MyData, pageKey: String): ImageViewerBuilder {
        // 数据提供者 一次加载 or 分页
        fun myDataProvider(clickedData: MyData): DataProvider {
            return if (loadAllAtOnce) {
                SimpleDataProvider(TestRepository.get().data)
            } else {
                 object : DataProvider {
                    override fun loadInitial(): List<Photo> = listOf(clickedData)
                    override fun loadAfter(key: Long, callback: (List<Photo>) -> Unit)
                        = TestRepository.get().api.asyncQueryAfter(key, PAGE_SIZE, callback)
                    override fun loadBefore(key: Long, callback: (List<Photo>) -> Unit)
                        = TestRepository.get().api.asyncQueryBefore(key, PAGE_SIZE, callback)
                }
            }
        }

        // viewer 构造的基本元素
        val builder = ImageViewerBuilder(
                context = context,
                initKey = clickedData.id,
                dataProvider = myDataProvider(clickedData),
                imageLoader = MyImageLoader(),
                transformer = MyTransformer(pageKey)
        )

        MyViewerCustomizer().process(context, builder) // 添加自定义业务逻辑和UI处理

        if (fullScreen) {
            builder.setViewerFactory(object : ImageViewerDialogFragment.Factory() {
                override fun build() = FullScreenImageViewerDialogFragment()
            })
        }
        return builder
    }

}

