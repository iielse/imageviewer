package com.github.iielse.imageviewer.demo

import android.util.LongSparseArray
import android.widget.ImageView
import com.github.iielse.imageviewer.core.Transformer

class MyTransformer : Transformer {
    override fun getView(key: Long): ImageView? = Trans.mapping[key]
}

object Trans {
     val mapping = LongSparseArray<ImageView>()

}