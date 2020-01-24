package com.github.iielse.imageviewer

import android.view.View

interface ImageViewerAdapterListener : PhotoView2.Listener {
    fun onInit(view: View)

    fun onLoadMore()
}