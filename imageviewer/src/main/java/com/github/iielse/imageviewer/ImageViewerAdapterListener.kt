package com.github.iielse.imageviewer

import android.widget.ImageView
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.widgets.PhotoView2


interface ImageViewerAdapterListener : PhotoView2.Listener {
    fun onInit(view: PhotoView2)
    fun onLoad(view: ImageView, item: Photo)
}