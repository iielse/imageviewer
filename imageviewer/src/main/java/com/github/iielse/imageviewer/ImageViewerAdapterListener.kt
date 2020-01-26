package com.github.iielse.imageviewer

import android.view.View
import android.widget.ImageView
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.widgets.PhotoView2


interface ImageViewerAdapterListener  {
    fun onInit(view: PhotoView2)
    fun onLoad(view: ImageView, item: Photo)
    fun onDrag(itemView: View, view: PhotoView2, fraction: Float) {}
    fun onRestore(itemView: View, view: PhotoView2, fraction: Float) {}
    fun onRelease(itemView: View, view: PhotoView2) {}
}