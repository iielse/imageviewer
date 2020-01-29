package github.iielse.imageviewer.demo

import android.util.LongSparseArray
import android.widget.ImageView
import com.github.iielse.imageviewer.core.Transformer

class MyTransformer(private val mapping: LongSparseArray<ImageView>) : Transformer {
    override fun getView(key: Long): ImageView? = mapping[key]
}