package com.github.iielse.imageviewer.demo.business

import android.view.View
import android.widget.ImageView
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.core.Transformer
import com.github.iielse.imageviewer.demo.R
import com.github.iielse.imageviewer.demo.core.viewer.MyImageLoader
import com.github.iielse.imageviewer.demo.core.viewer.provideViewerDataProvider
import com.github.iielse.imageviewer.demo.data.TestRepository

// 自定义Transition startView 尺寸/位置/加载模式
object CustomTransitionHelper {
    fun show(view: View) {
        val dataList = TestRepository.get().data
        val clickedData = dataList[dataList.size - 1 - (System.currentTimeMillis() % 10).toInt()]
        val builder = ImageViewerBuilder(
                context = view.context,
                initKey = clickedData.id,
                dataProvider = provideViewerDataProvider { dataList },
                imageLoader = MyImageLoader(),
                transformer = object : Transformer {
                    override fun getView(key: Long): ImageView? {
                        return fakeStartView(view)
                    }
                }
        )
        builder.show()
    }

    // 提供原图尺寸/位置/加载模式
    private fun fakeStartView(view: View): ImageView {
        val customWidth = view.width
        val customHeight = view.height
        val customLocation = IntArray(2).also { view.getLocationOnScreen(it) }
        val customScaleType = ImageView.ScaleType.CENTER_CROP
        return ImageView(view.context).apply {
            left = 0
            right = customWidth
            top = 0
            bottom = customHeight
            scaleType = customScaleType
            setTag(R.id.viewer_start_view_location_0, customLocation[0])
            setTag(R.id.viewer_start_view_location_1, customLocation[1])
        }
    }
}