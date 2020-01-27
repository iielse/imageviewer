package com.github.iielse.imageviewer

import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.widgets.PhotoView2

interface ImageViewerAdapterListener : PhotoView2.Listener {
    fun onInit(viewHolder: RecyclerView.ViewHolder)
}