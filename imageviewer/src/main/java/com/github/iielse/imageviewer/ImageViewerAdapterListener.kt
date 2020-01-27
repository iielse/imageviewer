package com.github.iielse.imageviewer

import com.github.iielse.imageviewer.widgets.PhotoView2

interface ImageViewerAdapterListener : PhotoView2.Listener {
    fun onInit(view: PhotoView2)
}