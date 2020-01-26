package com.github.iielse.imageviewer

import android.view.View
import android.widget.ImageView


interface ImageViewerAdapterListener : PhotoView2.Listener {
    fun onInit(view: View)

    fun onLoad(view: ImageView, item: Photo)
}