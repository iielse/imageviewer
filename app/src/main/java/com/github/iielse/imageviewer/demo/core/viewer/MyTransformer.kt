package com.github.iielse.imageviewer.demo.core.viewer

import android.util.LongSparseArray
import android.widget.ImageView
import com.github.iielse.imageviewer.core.Transformer
import com.github.iielse.imageviewer.demo.core.viewer.TransitionViewsRef.KEY_MAIN

class MyTransformer : Transformer {
    override fun getView(key: Long): ImageView? = TransitionViewsRef.provideTransitionViewsRef(KEY_MAIN)[key]
}

/**
 * 维护Transition过渡动画的缩略图和大图之间的映射关系. 需要在Activity/Fragment释放时刻.清空此界面的View引用
 */
object TransitionViewsRef {
    private val map = mutableMapOf<String, LongSparseArray<ImageView>?>() // 可能有多级页面
    const val KEY_MAIN = "page_main"

    fun provideTransitionViewsRef(key: String): LongSparseArray<ImageView> {
        return map[key] ?: LongSparseArray<ImageView>().also { map[key] = it }
    }

    // invoke when activity onDestroy or fragment onDestroyView
    fun releaseTransitionViewRef(key: String) {
        map[key] = null
    }
}