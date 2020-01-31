package com.github.iielse.imageviewer.demo

import android.graphics.Rect
import android.util.LongSparseArray
import android.widget.ImageView
import com.github.iielse.imageviewer.core.Transformer
import com.github.iielse.imageviewer.utils.log

class MyTransformer : Transformer {
    override fun getView(key: Long): ImageView? = Trans.mapping[key]
}

object Trans {
     val mapping = LongSparseArray<ImageView>()

}