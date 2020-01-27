package com.github.iielse.imageviewer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.iielse.imageviewer.widgets.PhotoView2

interface ImageViewerAdapterListener {
    fun onInit(viewHolder: RecyclerView.ViewHolder)
    fun onDrag(viewHolder: RecyclerView.ViewHolder, view: PhotoView2, fraction: Float)
    fun onRestore(viewHolder: RecyclerView.ViewHolder, view: PhotoView2, fraction: Float)
    fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View)
}