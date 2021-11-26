package com.github.iielse.imageviewer.demo.core.viewer

import android.view.View
import android.widget.ImageView
import com.github.iielse.imageviewer.core.Transformer
import com.github.iielse.imageviewer.demo.utils.isMainThread

class SimpleTransformer : Transformer {
    override fun getView(key: Long): ImageView? = ViewerTransitionHelper.provide(key)
}

/**
 * 维护Transition过渡动画的缩略图和大图之间的映射关系.
 */
object ViewerTransitionHelper {
    private val transition = HashMap<ImageView, Long>()
    fun put(photoId: Long, imageView: ImageView) {
        require(isMainThread())
        if (!imageView.isAttachedToWindow) return
        imageView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View?) = Unit
            override fun onViewDetachedFromWindow(p0: View?) {
                transition.remove(imageView)
                imageView.removeOnAttachStateChangeListener(this)
            }
        })
        transition[imageView] = photoId
    }

    fun provide(photoId: Long): ImageView? {
        transition.keys.forEach {
            if (transition[it] == photoId)
                return it
        }
        return null
    }
}